// Code generated by protoc-gen-gogo. DO NOT EDIT.
// source: mixer/adapter/kubernetesenv/template/template_handler_service.proto

/*
	Package adapter_template_kubernetes is a generated protocol buffer package.

	The `kubernetes` template holds data that controls the production of Kubernetes-specific
	attributes.

	The `kubernetes` template represents data used to generate kubernetes-derived attributes.

	The values provided controls the manner in which the kubernetesenv adapter discovers and
	generates values related to pod information.

	Example config:
	```yaml
	apiVersion: "config.istio.io/v1alpha2"
	kind: kubernetes
	metadata:
	  name: attributes
	  namespace: istio-system
	spec:
	  # Pass the required attribute data to the adapter
	  source_uid: source.uid | ""
	  source_ip: source.ip | ip("0.0.0.0") # default to unspecified ip addr
	  destination_uid: destination.uid | ""
	  destination_ip: destination.ip | ip("0.0.0.0") # default to unspecified ip addr
	  attribute_bindings:
	    # Fill the new attributes from the adapter produced output.
	    # $out refers to an instance of OutputTemplate message
	    source.ip: $out.source_pod_ip
	    source.labels: $out.source_labels
	    source.namespace: $out.source_namespace
	    source.service: $out.source_service
	    source.serviceAccount: $out.source_service_account_name
	    destination.ip: $out.destination_pod_ip
	    destination.labels: $out.destination_labels
	    destination.namespace: $out.destination_mamespace
	    destination.service: $out.destination_service
	    destination.serviceAccount: $out.destination_service_account_name
	```

	It is generated from these files:
		mixer/adapter/kubernetesenv/template/template_handler_service.proto

	It has these top-level messages:
		InstanceParam
*/
package adapter_template_kubernetes

import proto "github.com/gogo/protobuf/proto"
import fmt "fmt"
import math "math"
import _ "github.com/gogo/protobuf/gogoproto"
import _ "istio.io/api/mixer/adapter/model/v1beta1"

import strings "strings"
import reflect "reflect"
import github_com_gogo_protobuf_sortkeys "github.com/gogo/protobuf/sortkeys"

import io "io"

// Reference imports to suppress errors if they are not otherwise used.
var _ = proto.Marshal
var _ = fmt.Errorf
var _ = math.Inf

// This is a compile-time assertion to ensure that this generated file
// is compatible with the proto package it is being compiled against.
// A compilation error at this line likely means your copy of the
// proto package needs to be updated.
const _ = proto.GoGoProtoPackageIsVersion2 // please upgrade the proto package

// Represents instance configuration schema for 'kubernetes' template.
type InstanceParam struct {
	// Source pod's uid. Must be of the form: "kubernetes://pod.namespace"
	SourceUid string `protobuf:"bytes,1,opt,name=source_uid,json=sourceUid,proto3" json:"source_uid,omitempty"`
	// Source pod's ip.
	SourceIp string `protobuf:"bytes,2,opt,name=source_ip,json=sourceIp,proto3" json:"source_ip,omitempty"`
	// Destination pod's uid. Must be of the form: "kubernetes://pod.namespace"
	DestinationUid string `protobuf:"bytes,3,opt,name=destination_uid,json=destinationUid,proto3" json:"destination_uid,omitempty"`
	// Destination pod's ip.
	DestinationIp string `protobuf:"bytes,4,opt,name=destination_ip,json=destinationIp,proto3" json:"destination_ip,omitempty"`
	// Origin pod's uid. Must be of the form: "kubernetes://pod.namespace"
	OriginUid string `protobuf:"bytes,5,opt,name=origin_uid,json=originUid,proto3" json:"origin_uid,omitempty"`
	// Origin pod's ip.
	OriginIp string `protobuf:"bytes,6,opt,name=origin_ip,json=originIp,proto3" json:"origin_ip,omitempty"`
	// Attribute names to expression mapping. These expressions can use the fields from the output object
	// returned by the attribute producing adapters using $out.<fieldName> notation. For example:
	// source.ip : $out.source_pod_ip
	// In the above example, source.ip attribute will be added to the existing attribute list and its value will be set to
	// the value of source_pod_ip field of the output returned by the adapter.
	AttributeBindings map[string]string `protobuf:"bytes,72295728,rep,name=attribute_bindings,json=attributeBindings" json:"attribute_bindings,omitempty" protobuf_key:"bytes,1,opt,name=key,proto3" protobuf_val:"bytes,2,opt,name=value,proto3"`
}

func (m *InstanceParam) Reset()      { *m = InstanceParam{} }
func (*InstanceParam) ProtoMessage() {}
func (*InstanceParam) Descriptor() ([]byte, []int) {
	return fileDescriptorTemplateHandlerService, []int{0}
}

func init() {
	proto.RegisterType((*InstanceParam)(nil), "adapter.template.kubernetes.InstanceParam")
}
func (m *InstanceParam) Marshal() (dAtA []byte, err error) {
	size := m.Size()
	dAtA = make([]byte, size)
	n, err := m.MarshalTo(dAtA)
	if err != nil {
		return nil, err
	}
	return dAtA[:n], nil
}

func (m *InstanceParam) MarshalTo(dAtA []byte) (int, error) {
	var i int
	_ = i
	var l int
	_ = l
	if len(m.SourceUid) > 0 {
		dAtA[i] = 0xa
		i++
		i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(m.SourceUid)))
		i += copy(dAtA[i:], m.SourceUid)
	}
	if len(m.SourceIp) > 0 {
		dAtA[i] = 0x12
		i++
		i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(m.SourceIp)))
		i += copy(dAtA[i:], m.SourceIp)
	}
	if len(m.DestinationUid) > 0 {
		dAtA[i] = 0x1a
		i++
		i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(m.DestinationUid)))
		i += copy(dAtA[i:], m.DestinationUid)
	}
	if len(m.DestinationIp) > 0 {
		dAtA[i] = 0x22
		i++
		i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(m.DestinationIp)))
		i += copy(dAtA[i:], m.DestinationIp)
	}
	if len(m.OriginUid) > 0 {
		dAtA[i] = 0x2a
		i++
		i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(m.OriginUid)))
		i += copy(dAtA[i:], m.OriginUid)
	}
	if len(m.OriginIp) > 0 {
		dAtA[i] = 0x32
		i++
		i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(m.OriginIp)))
		i += copy(dAtA[i:], m.OriginIp)
	}
	if len(m.AttributeBindings) > 0 {
		for k, _ := range m.AttributeBindings {
			dAtA[i] = 0x82
			i++
			dAtA[i] = 0xd3
			i++
			dAtA[i] = 0xe4
			i++
			dAtA[i] = 0x93
			i++
			dAtA[i] = 0x2
			i++
			v := m.AttributeBindings[k]
			mapSize := 1 + len(k) + sovTemplateHandlerService(uint64(len(k))) + 1 + len(v) + sovTemplateHandlerService(uint64(len(v)))
			i = encodeVarintTemplateHandlerService(dAtA, i, uint64(mapSize))
			dAtA[i] = 0xa
			i++
			i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(k)))
			i += copy(dAtA[i:], k)
			dAtA[i] = 0x12
			i++
			i = encodeVarintTemplateHandlerService(dAtA, i, uint64(len(v)))
			i += copy(dAtA[i:], v)
		}
	}
	return i, nil
}

func encodeVarintTemplateHandlerService(dAtA []byte, offset int, v uint64) int {
	for v >= 1<<7 {
		dAtA[offset] = uint8(v&0x7f | 0x80)
		v >>= 7
		offset++
	}
	dAtA[offset] = uint8(v)
	return offset + 1
}
func (m *InstanceParam) Size() (n int) {
	var l int
	_ = l
	l = len(m.SourceUid)
	if l > 0 {
		n += 1 + l + sovTemplateHandlerService(uint64(l))
	}
	l = len(m.SourceIp)
	if l > 0 {
		n += 1 + l + sovTemplateHandlerService(uint64(l))
	}
	l = len(m.DestinationUid)
	if l > 0 {
		n += 1 + l + sovTemplateHandlerService(uint64(l))
	}
	l = len(m.DestinationIp)
	if l > 0 {
		n += 1 + l + sovTemplateHandlerService(uint64(l))
	}
	l = len(m.OriginUid)
	if l > 0 {
		n += 1 + l + sovTemplateHandlerService(uint64(l))
	}
	l = len(m.OriginIp)
	if l > 0 {
		n += 1 + l + sovTemplateHandlerService(uint64(l))
	}
	if len(m.AttributeBindings) > 0 {
		for k, v := range m.AttributeBindings {
			_ = k
			_ = v
			mapEntrySize := 1 + len(k) + sovTemplateHandlerService(uint64(len(k))) + 1 + len(v) + sovTemplateHandlerService(uint64(len(v)))
			n += mapEntrySize + 5 + sovTemplateHandlerService(uint64(mapEntrySize))
		}
	}
	return n
}

func sovTemplateHandlerService(x uint64) (n int) {
	for {
		n++
		x >>= 7
		if x == 0 {
			break
		}
	}
	return n
}
func sozTemplateHandlerService(x uint64) (n int) {
	return sovTemplateHandlerService(uint64((x << 1) ^ uint64((int64(x) >> 63))))
}
func (this *InstanceParam) String() string {
	if this == nil {
		return "nil"
	}
	keysForAttributeBindings := make([]string, 0, len(this.AttributeBindings))
	for k, _ := range this.AttributeBindings {
		keysForAttributeBindings = append(keysForAttributeBindings, k)
	}
	github_com_gogo_protobuf_sortkeys.Strings(keysForAttributeBindings)
	mapStringForAttributeBindings := "map[string]string{"
	for _, k := range keysForAttributeBindings {
		mapStringForAttributeBindings += fmt.Sprintf("%v: %v,", k, this.AttributeBindings[k])
	}
	mapStringForAttributeBindings += "}"
	s := strings.Join([]string{`&InstanceParam{`,
		`SourceUid:` + fmt.Sprintf("%v", this.SourceUid) + `,`,
		`SourceIp:` + fmt.Sprintf("%v", this.SourceIp) + `,`,
		`DestinationUid:` + fmt.Sprintf("%v", this.DestinationUid) + `,`,
		`DestinationIp:` + fmt.Sprintf("%v", this.DestinationIp) + `,`,
		`OriginUid:` + fmt.Sprintf("%v", this.OriginUid) + `,`,
		`OriginIp:` + fmt.Sprintf("%v", this.OriginIp) + `,`,
		`AttributeBindings:` + mapStringForAttributeBindings + `,`,
		`}`,
	}, "")
	return s
}
func valueToStringTemplateHandlerService(v interface{}) string {
	rv := reflect.ValueOf(v)
	if rv.IsNil() {
		return "nil"
	}
	pv := reflect.Indirect(rv).Interface()
	return fmt.Sprintf("*%v", pv)
}
func (m *InstanceParam) Unmarshal(dAtA []byte) error {
	l := len(dAtA)
	iNdEx := 0
	for iNdEx < l {
		preIndex := iNdEx
		var wire uint64
		for shift := uint(0); ; shift += 7 {
			if shift >= 64 {
				return ErrIntOverflowTemplateHandlerService
			}
			if iNdEx >= l {
				return io.ErrUnexpectedEOF
			}
			b := dAtA[iNdEx]
			iNdEx++
			wire |= (uint64(b) & 0x7F) << shift
			if b < 0x80 {
				break
			}
		}
		fieldNum := int32(wire >> 3)
		wireType := int(wire & 0x7)
		if wireType == 4 {
			return fmt.Errorf("proto: InstanceParam: wiretype end group for non-group")
		}
		if fieldNum <= 0 {
			return fmt.Errorf("proto: InstanceParam: illegal tag %d (wire type %d)", fieldNum, wire)
		}
		switch fieldNum {
		case 1:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field SourceUid", wireType)
			}
			var stringLen uint64
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				stringLen |= (uint64(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			intStringLen := int(stringLen)
			if intStringLen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + intStringLen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			m.SourceUid = string(dAtA[iNdEx:postIndex])
			iNdEx = postIndex
		case 2:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field SourceIp", wireType)
			}
			var stringLen uint64
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				stringLen |= (uint64(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			intStringLen := int(stringLen)
			if intStringLen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + intStringLen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			m.SourceIp = string(dAtA[iNdEx:postIndex])
			iNdEx = postIndex
		case 3:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field DestinationUid", wireType)
			}
			var stringLen uint64
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				stringLen |= (uint64(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			intStringLen := int(stringLen)
			if intStringLen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + intStringLen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			m.DestinationUid = string(dAtA[iNdEx:postIndex])
			iNdEx = postIndex
		case 4:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field DestinationIp", wireType)
			}
			var stringLen uint64
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				stringLen |= (uint64(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			intStringLen := int(stringLen)
			if intStringLen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + intStringLen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			m.DestinationIp = string(dAtA[iNdEx:postIndex])
			iNdEx = postIndex
		case 5:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field OriginUid", wireType)
			}
			var stringLen uint64
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				stringLen |= (uint64(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			intStringLen := int(stringLen)
			if intStringLen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + intStringLen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			m.OriginUid = string(dAtA[iNdEx:postIndex])
			iNdEx = postIndex
		case 6:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field OriginIp", wireType)
			}
			var stringLen uint64
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				stringLen |= (uint64(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			intStringLen := int(stringLen)
			if intStringLen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + intStringLen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			m.OriginIp = string(dAtA[iNdEx:postIndex])
			iNdEx = postIndex
		case 72295728:
			if wireType != 2 {
				return fmt.Errorf("proto: wrong wireType = %d for field AttributeBindings", wireType)
			}
			var msglen int
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				msglen |= (int(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			if msglen < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			postIndex := iNdEx + msglen
			if postIndex > l {
				return io.ErrUnexpectedEOF
			}
			if m.AttributeBindings == nil {
				m.AttributeBindings = make(map[string]string)
			}
			var mapkey string
			var mapvalue string
			for iNdEx < postIndex {
				entryPreIndex := iNdEx
				var wire uint64
				for shift := uint(0); ; shift += 7 {
					if shift >= 64 {
						return ErrIntOverflowTemplateHandlerService
					}
					if iNdEx >= l {
						return io.ErrUnexpectedEOF
					}
					b := dAtA[iNdEx]
					iNdEx++
					wire |= (uint64(b) & 0x7F) << shift
					if b < 0x80 {
						break
					}
				}
				fieldNum := int32(wire >> 3)
				if fieldNum == 1 {
					var stringLenmapkey uint64
					for shift := uint(0); ; shift += 7 {
						if shift >= 64 {
							return ErrIntOverflowTemplateHandlerService
						}
						if iNdEx >= l {
							return io.ErrUnexpectedEOF
						}
						b := dAtA[iNdEx]
						iNdEx++
						stringLenmapkey |= (uint64(b) & 0x7F) << shift
						if b < 0x80 {
							break
						}
					}
					intStringLenmapkey := int(stringLenmapkey)
					if intStringLenmapkey < 0 {
						return ErrInvalidLengthTemplateHandlerService
					}
					postStringIndexmapkey := iNdEx + intStringLenmapkey
					if postStringIndexmapkey > l {
						return io.ErrUnexpectedEOF
					}
					mapkey = string(dAtA[iNdEx:postStringIndexmapkey])
					iNdEx = postStringIndexmapkey
				} else if fieldNum == 2 {
					var stringLenmapvalue uint64
					for shift := uint(0); ; shift += 7 {
						if shift >= 64 {
							return ErrIntOverflowTemplateHandlerService
						}
						if iNdEx >= l {
							return io.ErrUnexpectedEOF
						}
						b := dAtA[iNdEx]
						iNdEx++
						stringLenmapvalue |= (uint64(b) & 0x7F) << shift
						if b < 0x80 {
							break
						}
					}
					intStringLenmapvalue := int(stringLenmapvalue)
					if intStringLenmapvalue < 0 {
						return ErrInvalidLengthTemplateHandlerService
					}
					postStringIndexmapvalue := iNdEx + intStringLenmapvalue
					if postStringIndexmapvalue > l {
						return io.ErrUnexpectedEOF
					}
					mapvalue = string(dAtA[iNdEx:postStringIndexmapvalue])
					iNdEx = postStringIndexmapvalue
				} else {
					iNdEx = entryPreIndex
					skippy, err := skipTemplateHandlerService(dAtA[iNdEx:])
					if err != nil {
						return err
					}
					if skippy < 0 {
						return ErrInvalidLengthTemplateHandlerService
					}
					if (iNdEx + skippy) > postIndex {
						return io.ErrUnexpectedEOF
					}
					iNdEx += skippy
				}
			}
			m.AttributeBindings[mapkey] = mapvalue
			iNdEx = postIndex
		default:
			iNdEx = preIndex
			skippy, err := skipTemplateHandlerService(dAtA[iNdEx:])
			if err != nil {
				return err
			}
			if skippy < 0 {
				return ErrInvalidLengthTemplateHandlerService
			}
			if (iNdEx + skippy) > l {
				return io.ErrUnexpectedEOF
			}
			iNdEx += skippy
		}
	}

	if iNdEx > l {
		return io.ErrUnexpectedEOF
	}
	return nil
}
func skipTemplateHandlerService(dAtA []byte) (n int, err error) {
	l := len(dAtA)
	iNdEx := 0
	for iNdEx < l {
		var wire uint64
		for shift := uint(0); ; shift += 7 {
			if shift >= 64 {
				return 0, ErrIntOverflowTemplateHandlerService
			}
			if iNdEx >= l {
				return 0, io.ErrUnexpectedEOF
			}
			b := dAtA[iNdEx]
			iNdEx++
			wire |= (uint64(b) & 0x7F) << shift
			if b < 0x80 {
				break
			}
		}
		wireType := int(wire & 0x7)
		switch wireType {
		case 0:
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return 0, ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return 0, io.ErrUnexpectedEOF
				}
				iNdEx++
				if dAtA[iNdEx-1] < 0x80 {
					break
				}
			}
			return iNdEx, nil
		case 1:
			iNdEx += 8
			return iNdEx, nil
		case 2:
			var length int
			for shift := uint(0); ; shift += 7 {
				if shift >= 64 {
					return 0, ErrIntOverflowTemplateHandlerService
				}
				if iNdEx >= l {
					return 0, io.ErrUnexpectedEOF
				}
				b := dAtA[iNdEx]
				iNdEx++
				length |= (int(b) & 0x7F) << shift
				if b < 0x80 {
					break
				}
			}
			iNdEx += length
			if length < 0 {
				return 0, ErrInvalidLengthTemplateHandlerService
			}
			return iNdEx, nil
		case 3:
			for {
				var innerWire uint64
				var start int = iNdEx
				for shift := uint(0); ; shift += 7 {
					if shift >= 64 {
						return 0, ErrIntOverflowTemplateHandlerService
					}
					if iNdEx >= l {
						return 0, io.ErrUnexpectedEOF
					}
					b := dAtA[iNdEx]
					iNdEx++
					innerWire |= (uint64(b) & 0x7F) << shift
					if b < 0x80 {
						break
					}
				}
				innerWireType := int(innerWire & 0x7)
				if innerWireType == 4 {
					break
				}
				next, err := skipTemplateHandlerService(dAtA[start:])
				if err != nil {
					return 0, err
				}
				iNdEx = start + next
			}
			return iNdEx, nil
		case 4:
			return iNdEx, nil
		case 5:
			iNdEx += 4
			return iNdEx, nil
		default:
			return 0, fmt.Errorf("proto: illegal wireType %d", wireType)
		}
	}
	panic("unreachable")
}

var (
	ErrInvalidLengthTemplateHandlerService = fmt.Errorf("proto: negative length found during unmarshaling")
	ErrIntOverflowTemplateHandlerService   = fmt.Errorf("proto: integer overflow")
)

func init() {
	proto.RegisterFile("mixer/adapter/kubernetesenv/template/template_handler_service.proto", fileDescriptorTemplateHandlerService)
}

var fileDescriptorTemplateHandlerService = []byte{
	// 412 bytes of a gzipped FileDescriptorProto
	0x1f, 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0xff, 0x7c, 0x91, 0x3f, 0x8b, 0xd4, 0x40,
	0x18, 0xc6, 0x33, 0x1b, 0xef, 0xf0, 0x46, 0xce, 0x3f, 0xc3, 0x21, 0x61, 0x0f, 0x87, 0xe3, 0x40,
	0xbc, 0x42, 0x12, 0x4e, 0x41, 0xc4, 0xee, 0x4e, 0x2d, 0xd2, 0xc9, 0x82, 0x75, 0x98, 0x6c, 0x5e,
	0xe2, 0xb0, 0xc9, 0xcc, 0x30, 0x33, 0x09, 0xbb, 0x9d, 0x1f, 0x41, 0xf4, 0x4b, 0x58, 0xfa, 0x01,
	0xfc, 0x00, 0x8b, 0xd5, 0x62, 0x65, 0x69, 0xa2, 0x85, 0xe5, 0x96, 0x96, 0x92, 0x4c, 0x56, 0x77,
	0x45, 0xec, 0xde, 0xf9, 0xbd, 0xcf, 0xfb, 0xe4, 0xe1, 0x09, 0x7e, 0x5a, 0xf2, 0x39, 0xe8, 0x88,
	0x65, 0x4c, 0x59, 0xd0, 0xd1, 0xac, 0x4a, 0x41, 0x0b, 0xb0, 0x60, 0x40, 0xd4, 0x91, 0x85, 0x52,
	0x15, 0xcc, 0xc2, 0xef, 0x21, 0x79, 0xc5, 0x44, 0x56, 0x80, 0x4e, 0x0c, 0xe8, 0x9a, 0x4f, 0x21,
	0x54, 0x5a, 0x5a, 0x49, 0x8e, 0x87, 0xf3, 0x70, 0xa3, 0x0b, 0xff, 0xf8, 0x8c, 0x8f, 0x72, 0x99,
	0xcb, 0x5e, 0x17, 0x75, 0x93, 0x3b, 0x19, 0xdf, 0xdf, 0xfd, 0x6e, 0x29, 0x33, 0x28, 0xa2, 0xfa,
	0x3c, 0x05, 0xcb, 0xce, 0x23, 0x98, 0x5b, 0x10, 0x86, 0x4b, 0x61, 0x9c, 0xfa, 0xf4, 0xad, 0x8f,
	0x0f, 0x63, 0x61, 0x2c, 0x13, 0x53, 0x78, 0xc1, 0x34, 0x2b, 0xc9, 0x1d, 0x8c, 0x8d, 0xac, 0xf4,
	0x14, 0x92, 0x8a, 0x67, 0x01, 0x3a, 0x41, 0x67, 0x07, 0x93, 0x03, 0x47, 0x5e, 0xf2, 0x8c, 0x1c,
	0xe3, 0xe1, 0x91, 0x70, 0x15, 0x8c, 0xfa, 0xed, 0x55, 0x07, 0x62, 0x45, 0xee, 0xe1, 0x1b, 0x19,
	0x18, 0xcb, 0x05, 0xb3, 0x5c, 0x8a, 0xde, 0xc0, 0xef, 0x25, 0xd7, 0xb7, 0x70, 0xe7, 0x72, 0x17,
	0x6f, 0x93, 0xce, 0xea, 0x4a, 0xaf, 0x3b, 0xdc, 0xa2, 0xb1, 0xea, 0xb2, 0x48, 0xcd, 0x73, 0xee,
	0xac, 0xf6, 0x5c, 0x16, 0x47, 0x86, 0x2c, 0xc3, 0x9a, 0xab, 0x60, 0xdf, 0x65, 0x71, 0x20, 0x56,
	0xc4, 0x60, 0xc2, 0xac, 0xd5, 0x3c, 0xad, 0x2c, 0x24, 0x29, 0x17, 0x19, 0x17, 0xb9, 0x09, 0x3e,
	0x7c, 0xfa, 0x78, 0x7a, 0xe2, 0x9f, 0x5d, 0x7b, 0x70, 0x11, 0xfe, 0xa7, 0xda, 0x70, 0xa7, 0x92,
	0xf0, 0x62, 0xe3, 0x73, 0x39, 0xd8, 0x3c, 0x17, 0x56, 0x2f, 0x26, 0xb7, 0xd8, 0xdf, 0x7c, 0xfc,
	0x0c, 0xdf, 0xfe, 0xb7, 0x98, 0xdc, 0xc4, 0xfe, 0x0c, 0x16, 0x43, 0x9f, 0xdd, 0x48, 0x8e, 0xf0,
	0x5e, 0xcd, 0x8a, 0x0a, 0x86, 0x16, 0xdd, 0xe3, 0xc9, 0xe8, 0x31, 0xba, 0x7c, 0xb4, 0x6c, 0xa8,
	0xb7, 0x6a, 0xa8, 0xf7, 0xa5, 0xa1, 0xde, 0xba, 0xa1, 0xde, 0xeb, 0x96, 0xa2, 0xf7, 0x2d, 0xf5,
	0x96, 0x2d, 0x45, 0xab, 0x96, 0xa2, 0xaf, 0x2d, 0x45, 0x3f, 0x5a, 0xea, 0xad, 0x5b, 0x8a, 0xde,
	0x7c, 0xa3, 0xde, 0xcf, 0xcf, 0xdf, 0xdf, 0x8d, 0xfc, 0x74, 0xbf, 0xff, 0xa7, 0x0f, 0x7f, 0x05,
	0x00, 0x00, 0xff, 0xff, 0xff, 0x79, 0x36, 0x8c, 0x7b, 0x02, 0x00, 0x00,
}
