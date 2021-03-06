/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.wire;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.wire.java.JavaGenerator;
import com.squareup.wire.schema.EnumType;
import com.squareup.wire.schema.Location;
import com.squareup.wire.schema.MessageType;
import com.squareup.wire.schema.ProtoFile;
import com.squareup.wire.schema.Schema;
import com.squareup.wire.schema.SchemaLoader;
import com.squareup.wire.schema.Type;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/** Compiler for Wire protocol buffers. */
public final class WireCompiler {
  private static final String CODE_GENERATED_BY_WIRE =
      "Code generated by Wire protocol buffer compiler, do not edit.";

  private final FileSystem fs;
  private final CommandLineOptions options;
  private final WireLogger log;

  /**
   * Runs the compiler.  See {@link CommandLineOptions} for command line options.
   */
  public static void main(String... args) throws IOException {
    try {
      WireCompiler.create(new CommandLineOptions(args)).compile();
    } catch (WireException e) {
      System.err.print("Fatal: ");
      e.printStackTrace(System.err);
      System.exit(1);
    }
  }

  static WireCompiler create(CommandLineOptions options) throws WireException {
    return new WireCompiler(options, FileSystems.getDefault(),
        new ConsoleWireLogger(options.quiet));
  }

  WireCompiler(CommandLineOptions options, FileSystem fs, WireLogger logger) throws WireException {
    this.options = options;
    this.fs = fs;
    this.log = logger;

    if (options.javaOut == null) {
      throw new WireException("Must specify " + CommandLineOptions.JAVA_OUT_FLAG + " flag");
    }
  }

  public void compile() throws IOException {
    SchemaLoader schemaLoader = new SchemaLoader();
    for (String protoPath : options.protoPaths) {
      schemaLoader.addDirectory(fs.getPath(protoPath));
    }
    for (String sourceFileName : options.sourceFileNames) {
      schemaLoader.addProto(fs.getPath(sourceFileName));
    }
    Schema schema = schemaLoader.load();

    if (!options.roots.isEmpty()) {
      log.info("Analyzing dependencies of root types.");
      schema = schema.retainRoots(options.roots);
    }

    JavaGenerator javaGenerator = JavaGenerator.get(schema)
        .withOptions(options.emitOptions, options.enumOptions);

    for (ProtoFile protoFile : schema.protoFiles()) {
      if (!options.sourceFileNames.contains(protoFile.location().path())) {
        continue; // Don't emit anything for files not explicitly compiled.
      }

      for (Type type : protoFile.types()) {
        ClassName javaTypeName = (ClassName) javaGenerator.typeName(type.name());
        TypeSpec typeSpec = type instanceof MessageType
            ? javaGenerator.generateMessage((MessageType) type)
            : javaGenerator.generateEnum((EnumType) type);
        writeJavaFile(javaTypeName, typeSpec, type.location());
      }

      if (!protoFile.extendList().isEmpty()) {
        ClassName javaTypeName = javaGenerator.extensionsClass(protoFile);
        TypeSpec typeSpec = javaGenerator.generateExtensionsClass(javaTypeName, protoFile);
        writeJavaFile(javaTypeName, typeSpec, protoFile.location());
      }
    }

    if (options.registryClass != null) {
      ClassName className = ClassName.bestGuess(options.registryClass);
      TypeSpec typeSpec = javaGenerator.generateRegistry(className);
      writeJavaFile(className, typeSpec, null);
    }
  }

  private void writeJavaFile(ClassName javaTypeName, TypeSpec typeSpec, Location location)
      throws IOException {
    JavaFile.Builder builder = JavaFile.builder(javaTypeName.packageName(), typeSpec)
        .addFileComment("$L", CODE_GENERATED_BY_WIRE);
    if (location != null) {
      builder.addFileComment("\nSource file: $L", location);
    }
    JavaFile javaFile = builder.build();

    Path path = fs.getPath(options.javaOut);
    log.artifact(path, javaFile);

    try {
      if (!options.dryRun) {
        javaFile.writeTo(path);
      }
    } catch (IOException e) {
      throw new IOException("Error emitting " + javaFile.packageName + "."
          + javaFile.typeSpec.name + " to " + options.javaOut, e);
    }
  }
}
