// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ../wire-runtime/src/test/proto/google/protobuf/descriptor.proto at 168:1
package com.google.protobuf;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.TagMap;
import com.squareup.wire.WireField;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Collections;
import java.util.List;

/**
 * Describes an enum type.
 */
public final class EnumDescriptorProto extends Message<EnumDescriptorProto> {
  public static final ProtoAdapter<EnumDescriptorProto> ADAPTER = ProtoAdapter.newMessageAdapter(EnumDescriptorProto.class);

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_NAME = "";

  public static final String DEFAULT_DOC = "";

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String name;

  /**
   * Doc string for generated code.
   */
  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String doc;

  @WireField(
      tag = 2,
      adapter = "com.google.protobuf.EnumValueDescriptorProto#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<EnumValueDescriptorProto> value;

  @WireField(
      tag = 3,
      adapter = "com.google.protobuf.EnumOptions#ADAPTER"
  )
  public final EnumOptions options;

  public EnumDescriptorProto(String name, String doc, List<EnumValueDescriptorProto> value, EnumOptions options) {
    this(name, doc, value, options, TagMap.EMPTY);
  }

  public EnumDescriptorProto(String name, String doc, List<EnumValueDescriptorProto> value, EnumOptions options, TagMap tagMap) {
    super(tagMap);
    this.name = name;
    this.doc = doc;
    this.value = immutableCopyOf(value);
    this.options = options;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof EnumDescriptorProto)) return false;
    EnumDescriptorProto o = (EnumDescriptorProto) other;
    return equals(tagMap(), o.tagMap())
        && equals(name, o.name)
        && equals(doc, o.doc)
        && equals(value, o.value)
        && equals(options, o.options);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = tagMap().hashCode();
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (doc != null ? doc.hashCode() : 0);
      result = result * 37 + (value != null ? value.hashCode() : 1);
      result = result * 37 + (options != null ? options.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends com.squareup.wire.Message.Builder<EnumDescriptorProto, Builder> {
    public String name;

    public String doc;

    public List<EnumValueDescriptorProto> value = Collections.emptyList();

    public EnumOptions options;

    public Builder() {
    }

    public Builder(EnumDescriptorProto message) {
      super(message);
      if (message == null) return;
      this.name = message.name;
      this.doc = message.doc;
      this.value = copyOf(message.value);
      this.options = message.options;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Doc string for generated code.
     */
    public Builder doc(String doc) {
      this.doc = doc;
      return this;
    }

    public Builder value(List<EnumValueDescriptorProto> value) {
      this.value = canonicalizeList(value);
      return this;
    }

    public Builder options(EnumOptions options) {
      this.options = options;
      return this;
    }

    @Override
    public EnumDescriptorProto build() {
      return new EnumDescriptorProto(name, doc, value, options, buildTagMap());
    }
  }
}
