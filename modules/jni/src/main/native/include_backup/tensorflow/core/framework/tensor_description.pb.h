// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: tensorflow/core/framework/tensor_description.proto

#ifndef GOOGLE_PROTOBUF_INCLUDED_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto
#define GOOGLE_PROTOBUF_INCLUDED_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto

#include <limits>
#include <string>

#include <google/protobuf/port_def.inc>
#if PROTOBUF_VERSION < 3009000
#error This file was generated by a newer version of protoc which is
#error incompatible with your Protocol Buffer headers. Please update
#error your headers.
#endif
#if 3009002 < PROTOBUF_MIN_PROTOC_VERSION
#error This file was generated by an older version of protoc which is
#error incompatible with your Protocol Buffer headers. Please
#error regenerate this file with a newer version of protoc.
#endif

#include <google/protobuf/port_undef.inc>
#include <google/protobuf/io/coded_stream.h>
#include <google/protobuf/arena.h>
#include <google/protobuf/arenastring.h>
#include <google/protobuf/generated_message_table_driven.h>
#include <google/protobuf/generated_message_util.h>
#include <google/protobuf/inlined_string_field.h>
#include <google/protobuf/metadata.h>
#include <google/protobuf/generated_message_reflection.h>
#include <google/protobuf/message.h>
#include <google/protobuf/repeated_field.h>  // IWYU pragma: export
#include <google/protobuf/extension_set.h>  // IWYU pragma: export
#include <google/protobuf/unknown_field_set.h>
#include "tensorflow/core/framework/allocation_description.pb.h"
#include "tensorflow/core/framework/tensor_shape.pb.h"
#include "tensorflow/core/framework/types.pb.h"
// @@protoc_insertion_point(includes)
#include <google/protobuf/port_def.inc>
#define PROTOBUF_INTERNAL_EXPORT_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto
PROTOBUF_NAMESPACE_OPEN
namespace internal {
class AnyMetadata;
}  // namespace internal
PROTOBUF_NAMESPACE_CLOSE

// Internal implementation detail -- do not use these members.
struct TableStruct_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto {
  static const ::PROTOBUF_NAMESPACE_ID::internal::ParseTableField entries[]
    PROTOBUF_SECTION_VARIABLE(protodesc_cold);
  static const ::PROTOBUF_NAMESPACE_ID::internal::AuxillaryParseTableField aux[]
    PROTOBUF_SECTION_VARIABLE(protodesc_cold);
  static const ::PROTOBUF_NAMESPACE_ID::internal::ParseTable schema[1]
    PROTOBUF_SECTION_VARIABLE(protodesc_cold);
  static const ::PROTOBUF_NAMESPACE_ID::internal::FieldMetadata field_metadata[];
  static const ::PROTOBUF_NAMESPACE_ID::internal::SerializationTable serialization_table[];
  static const ::PROTOBUF_NAMESPACE_ID::uint32 offsets[];
};
extern const ::PROTOBUF_NAMESPACE_ID::internal::DescriptorTable descriptor_table_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto;
namespace tensorflow {
class TensorDescription;
class TensorDescriptionDefaultTypeInternal;
extern TensorDescriptionDefaultTypeInternal _TensorDescription_default_instance_;
}  // namespace tensorflow
PROTOBUF_NAMESPACE_OPEN
template<> ::tensorflow::TensorDescription* Arena::CreateMaybeMessage<::tensorflow::TensorDescription>(Arena*);
PROTOBUF_NAMESPACE_CLOSE
namespace tensorflow {

// ===================================================================

class TensorDescription :
    public ::PROTOBUF_NAMESPACE_ID::Message /* @@protoc_insertion_point(class_definition:tensorflow.TensorDescription) */ {
 public:
  TensorDescription();
  virtual ~TensorDescription();

  TensorDescription(const TensorDescription& from);
  TensorDescription(TensorDescription&& from) noexcept
    : TensorDescription() {
    *this = ::std::move(from);
  }

  inline TensorDescription& operator=(const TensorDescription& from) {
    CopyFrom(from);
    return *this;
  }
  inline TensorDescription& operator=(TensorDescription&& from) noexcept {
    if (GetArenaNoVirtual() == from.GetArenaNoVirtual()) {
      if (this != &from) InternalSwap(&from);
    } else {
      CopyFrom(from);
    }
    return *this;
  }

  inline ::PROTOBUF_NAMESPACE_ID::Arena* GetArena() const final {
    return GetArenaNoVirtual();
  }
  inline void* GetMaybeArenaPointer() const final {
    return MaybeArenaPtr();
  }
  static const ::PROTOBUF_NAMESPACE_ID::Descriptor* descriptor() {
    return GetDescriptor();
  }
  static const ::PROTOBUF_NAMESPACE_ID::Descriptor* GetDescriptor() {
    return GetMetadataStatic().descriptor;
  }
  static const ::PROTOBUF_NAMESPACE_ID::Reflection* GetReflection() {
    return GetMetadataStatic().reflection;
  }
  static const TensorDescription& default_instance();

  static void InitAsDefaultInstance();  // FOR INTERNAL USE ONLY
  static inline const TensorDescription* internal_default_instance() {
    return reinterpret_cast<const TensorDescription*>(
               &_TensorDescription_default_instance_);
  }
  static constexpr int kIndexInFileMessages =
    0;

  friend void swap(TensorDescription& a, TensorDescription& b) {
    a.Swap(&b);
  }
  inline void Swap(TensorDescription* other) {
    if (other == this) return;
    if (GetArenaNoVirtual() == other->GetArenaNoVirtual()) {
      InternalSwap(other);
    } else {
      ::PROTOBUF_NAMESPACE_ID::internal::GenericSwap(this, other);
    }
  }
  void UnsafeArenaSwap(TensorDescription* other) {
    if (other == this) return;
    GOOGLE_DCHECK(GetArenaNoVirtual() == other->GetArenaNoVirtual());
    InternalSwap(other);
  }

  // implements Message ----------------------------------------------

  inline TensorDescription* New() const final {
    return CreateMaybeMessage<TensorDescription>(nullptr);
  }

  TensorDescription* New(::PROTOBUF_NAMESPACE_ID::Arena* arena) const final {
    return CreateMaybeMessage<TensorDescription>(arena);
  }
  void CopyFrom(const ::PROTOBUF_NAMESPACE_ID::Message& from) final;
  void MergeFrom(const ::PROTOBUF_NAMESPACE_ID::Message& from) final;
  void CopyFrom(const TensorDescription& from);
  void MergeFrom(const TensorDescription& from);
  PROTOBUF_ATTRIBUTE_REINITIALIZES void Clear() final;
  bool IsInitialized() const final;

  size_t ByteSizeLong() const final;
  #if GOOGLE_PROTOBUF_ENABLE_EXPERIMENTAL_PARSER
  const char* _InternalParse(const char* ptr, ::PROTOBUF_NAMESPACE_ID::internal::ParseContext* ctx) final;
  #else
  bool MergePartialFromCodedStream(
      ::PROTOBUF_NAMESPACE_ID::io::CodedInputStream* input) final;
  #endif  // GOOGLE_PROTOBUF_ENABLE_EXPERIMENTAL_PARSER
  void SerializeWithCachedSizes(
      ::PROTOBUF_NAMESPACE_ID::io::CodedOutputStream* output) const final;
  ::PROTOBUF_NAMESPACE_ID::uint8* InternalSerializeWithCachedSizesToArray(
      ::PROTOBUF_NAMESPACE_ID::uint8* target) const final;
  int GetCachedSize() const final { return _cached_size_.Get(); }

  private:
  inline void SharedCtor();
  inline void SharedDtor();
  void SetCachedSize(int size) const final;
  void InternalSwap(TensorDescription* other);
  friend class ::PROTOBUF_NAMESPACE_ID::internal::AnyMetadata;
  static ::PROTOBUF_NAMESPACE_ID::StringPiece FullMessageName() {
    return "tensorflow.TensorDescription";
  }
  protected:
  explicit TensorDescription(::PROTOBUF_NAMESPACE_ID::Arena* arena);
  private:
  static void ArenaDtor(void* object);
  inline void RegisterArenaDtor(::PROTOBUF_NAMESPACE_ID::Arena* arena);
  private:
  inline ::PROTOBUF_NAMESPACE_ID::Arena* GetArenaNoVirtual() const {
    return _internal_metadata_.arena();
  }
  inline void* MaybeArenaPtr() const {
    return _internal_metadata_.raw_arena_ptr();
  }
  public:

  ::PROTOBUF_NAMESPACE_ID::Metadata GetMetadata() const final;
  private:
  static ::PROTOBUF_NAMESPACE_ID::Metadata GetMetadataStatic() {
    ::PROTOBUF_NAMESPACE_ID::internal::AssignDescriptors(&::descriptor_table_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto);
    return ::descriptor_table_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto.file_level_metadata[kIndexInFileMessages];
  }

  public:

  // nested types ----------------------------------------------------

  // accessors -------------------------------------------------------

  enum : int {
    kShapeFieldNumber = 2,
    kAllocationDescriptionFieldNumber = 4,
    kDtypeFieldNumber = 1,
  };
  // .tensorflow.TensorShapeProto shape = 2;
  bool has_shape() const;
  void clear_shape();
  const ::tensorflow::TensorShapeProto& shape() const;
  ::tensorflow::TensorShapeProto* release_shape();
  ::tensorflow::TensorShapeProto* mutable_shape();
  void set_allocated_shape(::tensorflow::TensorShapeProto* shape);
  void unsafe_arena_set_allocated_shape(
      ::tensorflow::TensorShapeProto* shape);
  ::tensorflow::TensorShapeProto* unsafe_arena_release_shape();

  // .tensorflow.AllocationDescription allocation_description = 4;
  bool has_allocation_description() const;
  void clear_allocation_description();
  const ::tensorflow::AllocationDescription& allocation_description() const;
  ::tensorflow::AllocationDescription* release_allocation_description();
  ::tensorflow::AllocationDescription* mutable_allocation_description();
  void set_allocated_allocation_description(::tensorflow::AllocationDescription* allocation_description);
  void unsafe_arena_set_allocated_allocation_description(
      ::tensorflow::AllocationDescription* allocation_description);
  ::tensorflow::AllocationDescription* unsafe_arena_release_allocation_description();

  // .tensorflow.DataType dtype = 1;
  void clear_dtype();
  ::tensorflow::DataType dtype() const;
  void set_dtype(::tensorflow::DataType value);

  // @@protoc_insertion_point(class_scope:tensorflow.TensorDescription)
 private:
  class _Internal;

  ::PROTOBUF_NAMESPACE_ID::internal::InternalMetadataWithArena _internal_metadata_;
  template <typename T> friend class ::PROTOBUF_NAMESPACE_ID::Arena::InternalHelper;
  typedef void InternalArenaConstructable_;
  typedef void DestructorSkippable_;
  ::tensorflow::TensorShapeProto* shape_;
  ::tensorflow::AllocationDescription* allocation_description_;
  int dtype_;
  mutable ::PROTOBUF_NAMESPACE_ID::internal::CachedSize _cached_size_;
  friend struct ::TableStruct_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto;
};
// ===================================================================


// ===================================================================

#ifdef __GNUC__
  #pragma GCC diagnostic push
  #pragma GCC diagnostic ignored "-Wstrict-aliasing"
#endif  // __GNUC__
// TensorDescription

// .tensorflow.DataType dtype = 1;
inline void TensorDescription::clear_dtype() {
  dtype_ = 0;
}
inline ::tensorflow::DataType TensorDescription::dtype() const {
  // @@protoc_insertion_point(field_get:tensorflow.TensorDescription.dtype)
  return static_cast< ::tensorflow::DataType >(dtype_);
}
inline void TensorDescription::set_dtype(::tensorflow::DataType value) {
  
  dtype_ = value;
  // @@protoc_insertion_point(field_set:tensorflow.TensorDescription.dtype)
}

// .tensorflow.TensorShapeProto shape = 2;
inline bool TensorDescription::has_shape() const {
  return this != internal_default_instance() && shape_ != nullptr;
}
inline const ::tensorflow::TensorShapeProto& TensorDescription::shape() const {
  const ::tensorflow::TensorShapeProto* p = shape_;
  // @@protoc_insertion_point(field_get:tensorflow.TensorDescription.shape)
  return p != nullptr ? *p : *reinterpret_cast<const ::tensorflow::TensorShapeProto*>(
      &::tensorflow::_TensorShapeProto_default_instance_);
}
inline ::tensorflow::TensorShapeProto* TensorDescription::release_shape() {
  // @@protoc_insertion_point(field_release:tensorflow.TensorDescription.shape)
  
  ::tensorflow::TensorShapeProto* temp = shape_;
  if (GetArenaNoVirtual() != nullptr) {
    temp = ::PROTOBUF_NAMESPACE_ID::internal::DuplicateIfNonNull(temp);
  }
  shape_ = nullptr;
  return temp;
}
inline ::tensorflow::TensorShapeProto* TensorDescription::unsafe_arena_release_shape() {
  // @@protoc_insertion_point(field_unsafe_arena_release:tensorflow.TensorDescription.shape)
  
  ::tensorflow::TensorShapeProto* temp = shape_;
  shape_ = nullptr;
  return temp;
}
inline ::tensorflow::TensorShapeProto* TensorDescription::mutable_shape() {
  
  if (shape_ == nullptr) {
    auto* p = CreateMaybeMessage<::tensorflow::TensorShapeProto>(GetArenaNoVirtual());
    shape_ = p;
  }
  // @@protoc_insertion_point(field_mutable:tensorflow.TensorDescription.shape)
  return shape_;
}
inline void TensorDescription::set_allocated_shape(::tensorflow::TensorShapeProto* shape) {
  ::PROTOBUF_NAMESPACE_ID::Arena* message_arena = GetArenaNoVirtual();
  if (message_arena == nullptr) {
    delete reinterpret_cast< ::PROTOBUF_NAMESPACE_ID::MessageLite*>(shape_);
  }
  if (shape) {
    ::PROTOBUF_NAMESPACE_ID::Arena* submessage_arena =
      reinterpret_cast<::PROTOBUF_NAMESPACE_ID::MessageLite*>(shape)->GetArena();
    if (message_arena != submessage_arena) {
      shape = ::PROTOBUF_NAMESPACE_ID::internal::GetOwnedMessage(
          message_arena, shape, submessage_arena);
    }
    
  } else {
    
  }
  shape_ = shape;
  // @@protoc_insertion_point(field_set_allocated:tensorflow.TensorDescription.shape)
}

// .tensorflow.AllocationDescription allocation_description = 4;
inline bool TensorDescription::has_allocation_description() const {
  return this != internal_default_instance() && allocation_description_ != nullptr;
}
inline const ::tensorflow::AllocationDescription& TensorDescription::allocation_description() const {
  const ::tensorflow::AllocationDescription* p = allocation_description_;
  // @@protoc_insertion_point(field_get:tensorflow.TensorDescription.allocation_description)
  return p != nullptr ? *p : *reinterpret_cast<const ::tensorflow::AllocationDescription*>(
      &::tensorflow::_AllocationDescription_default_instance_);
}
inline ::tensorflow::AllocationDescription* TensorDescription::release_allocation_description() {
  // @@protoc_insertion_point(field_release:tensorflow.TensorDescription.allocation_description)
  
  ::tensorflow::AllocationDescription* temp = allocation_description_;
  if (GetArenaNoVirtual() != nullptr) {
    temp = ::PROTOBUF_NAMESPACE_ID::internal::DuplicateIfNonNull(temp);
  }
  allocation_description_ = nullptr;
  return temp;
}
inline ::tensorflow::AllocationDescription* TensorDescription::unsafe_arena_release_allocation_description() {
  // @@protoc_insertion_point(field_unsafe_arena_release:tensorflow.TensorDescription.allocation_description)
  
  ::tensorflow::AllocationDescription* temp = allocation_description_;
  allocation_description_ = nullptr;
  return temp;
}
inline ::tensorflow::AllocationDescription* TensorDescription::mutable_allocation_description() {
  
  if (allocation_description_ == nullptr) {
    auto* p = CreateMaybeMessage<::tensorflow::AllocationDescription>(GetArenaNoVirtual());
    allocation_description_ = p;
  }
  // @@protoc_insertion_point(field_mutable:tensorflow.TensorDescription.allocation_description)
  return allocation_description_;
}
inline void TensorDescription::set_allocated_allocation_description(::tensorflow::AllocationDescription* allocation_description) {
  ::PROTOBUF_NAMESPACE_ID::Arena* message_arena = GetArenaNoVirtual();
  if (message_arena == nullptr) {
    delete reinterpret_cast< ::PROTOBUF_NAMESPACE_ID::MessageLite*>(allocation_description_);
  }
  if (allocation_description) {
    ::PROTOBUF_NAMESPACE_ID::Arena* submessage_arena =
      reinterpret_cast<::PROTOBUF_NAMESPACE_ID::MessageLite*>(allocation_description)->GetArena();
    if (message_arena != submessage_arena) {
      allocation_description = ::PROTOBUF_NAMESPACE_ID::internal::GetOwnedMessage(
          message_arena, allocation_description, submessage_arena);
    }
    
  } else {
    
  }
  allocation_description_ = allocation_description;
  // @@protoc_insertion_point(field_set_allocated:tensorflow.TensorDescription.allocation_description)
}

#ifdef __GNUC__
  #pragma GCC diagnostic pop
#endif  // __GNUC__

// @@protoc_insertion_point(namespace_scope)

}  // namespace tensorflow

// @@protoc_insertion_point(global_scope)

#include <google/protobuf/port_undef.inc>
#endif  // GOOGLE_PROTOBUF_INCLUDED_GOOGLE_PROTOBUF_INCLUDED_tensorflow_2fcore_2fframework_2ftensor_5fdescription_2eproto
