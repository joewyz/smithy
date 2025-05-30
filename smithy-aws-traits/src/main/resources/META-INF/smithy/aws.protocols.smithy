$version: "2.0"

namespace aws.protocols

/// An RPC-based protocol that sends JSON payloads. This protocol does not use
/// HTTP binding traits.
@protocolDefinition(
    traits: [
        timestampFormat
        cors
        endpoint
        hostLabel
    ]
)
@trait(selector: "service")
structure awsJson1_0 with [HttpConfiguration] {}

/// An RPC-based protocol that sends JSON payloads. This protocol does not use
/// HTTP binding traits.
@protocolDefinition(
    traits: [
        timestampFormat
        cors
        endpoint
        hostLabel
    ]
)
@trait(
    selector: "service"
)
structure awsJson1_1 with [HttpConfiguration] {}

/// Contains HTTP protocol configuration for HTTP-based protocols.
@private
@mixin(localTraits: [private])
structure HttpConfiguration {
    /// The priority ordered list of supported HTTP protocol versions.
    http: StringList

    /// The priority ordered list of supported HTTP protocol versions that
    /// are required when using event streams with the service. If not set,
    /// this value defaults to the value of the `http` member. Any entry in
    /// `eventStreamHttp` MUST also appear in `http`.
    eventStreamHttp: StringList
}

/// An RPC-based protocol that sends 'POST' requests in the body as
/// `x-www-form-urlencoded` strings and responses in XML documents. This
/// protocol does not use HTTP binding traits.
@deprecated
@protocolDefinition(
    traits: [
        timestampFormat
        cors
        endpoint
        hostLabel
        awsQueryError
        xmlAttribute
        xmlFlattened
        xmlName
        xmlNamespace
    ]
)
@trait(selector: "service [trait|xmlNamespace]")
@traitValidators(
    UnsupportedProtocolDocument: {
        selector: "~> member :test(> document)"
        message: "Document types are not supported with awsQuery"
    }
)
structure awsQuery {}

/// Provides the value in the 'Code' distinguishing field and HTTP response
/// code for an operation error.
@trait(
    selector: "structure [trait|error]",
    breakingChanges: [{change: "any"}]
)
structure awsQueryError {
    /// The value used to distinguish this error shape during serialization.
    @required
    code: String

    /// The HTTP response code used on a response containing this error shape.
    @required
    httpResponseCode: Integer
}

/// Enable backward compatibility when migrating from awsQuery to the awsJson
/// protocol or Smithy RPC v2 CBOR.
@trait(selector: "service :test([trait|aws.protocols#awsJson1_0], [trait|smithy.protocols#rpcv2Cbor])")
structure awsQueryCompatible {}

/// An RPC-based protocol that sends 'POST' requests in the body as Amazon EC2
/// formatted `x-www-form-urlencoded` strings and responses in XML documents.
/// This protocol does not use HTTP binding traits.
@deprecated
@protocolDefinition(
    traits: [
        timestampFormat
        cors
        endpoint
        hostLabel
        ec2QueryName
        xmlAttribute
        xmlFlattened
        xmlName
        xmlNamespace
    ]
)
@trait(selector: "service [trait|xmlNamespace]")
@traitValidators(
    UnsupportedProtocolDocument: {
        selector: "~> member :test(> document)"
        message: "Document types are not supported with ec2Query"
    }
)
structure ec2Query {}

/// Indicates the serialized name of a structure member when that structure is
/// serialized for the input of an EC2 operation.
@pattern("^[a-zA-Z_][a-zA-Z_0-9-]*$")
@trait(selector: "structure > member")
string ec2QueryName

/// Indicates that an operation supports checksum validation.
@trait(
    selector: "operation",
    breakingChanges: [
        {
            change: "remove",
            severity: "DANGER",
            message: """
                Removing the trait removes the ability for clients to do request or response checksums. The service \
                MUST continue to support old clients by supporting the `httpChecksum` trait."""
        },
        {
            change: "remove",
            path: "/requestAlgorithmMember",
            severity: "DANGER",
            message: """
                `requestAlgorithmMember` was removed, so newly generated clients will no longer be able to pick the \
                request checksum algorithms The service MUST continue to support old clients by supporting \
                `requestAlgorithmMember`."""
        },
        {
            change: "remove",
            path: "/requestValidationModeMember",
            severity: "DANGER",
            message: """
                `requestValidationModeMember` was removed, so newly generated clients will no longer validate response \
                checksums. The service MUST continue to support old clients by supporting \
                `requestValidationModeMember`."""
        },
        {
            change: "remove",
            path: "/responseAlgorithms/member",
            severity: "DANGER",
            message: """
                Members of `responseAlgorithms` were removed, so newly generated clients will no longer validate \
                response checksums for the removed algorithms. The service MUST continue to support old clients by \
                supporting removed compression algorithms."""
        }
    ]
)
@unstable
structure httpChecksum {
    /// Defines a top-level operation input member that is used to configure
    /// request checksum behavior.
    requestAlgorithmMember: String

    /// Indicates an operation requires a checksum in its HTTP request.
    requestChecksumRequired: Boolean

    /// Defines a top-level operation input member used to opt-in to response
    /// checksum validation.
    requestValidationModeMember: String

    /// Defines the checksum algorithms clients should look for when performing
    /// HTTP response checksum validation.
    responseAlgorithms: ChecksumAlgorithmSet
}

/// A RESTful protocol that sends JSON in structured payloads.
@protocolDefinition(
    traits: [
        timestampFormat
        cors
        endpoint
        hostLabel
        http
        httpError
        httpHeader
        httpLabel
        httpPayload
        httpPrefixHeaders
        httpQuery
        httpQueryParams
        httpResponseCode
        httpChecksumRequired
        jsonName
    ]
)
@trait(selector: "service")
structure restJson1 with [HttpConfiguration] {}

/// A RESTful protocol that sends XML in structured payloads.
@deprecated
@protocolDefinition(
    traits: [
        timestampFormat
        cors
        endpoint
        hostLabel
        http
        httpError
        httpHeader
        httpLabel
        httpPayload
        httpPrefixHeaders
        httpQuery
        httpQueryParams
        httpResponseCode
        httpChecksumRequired
        xmlAttribute
        xmlFlattened
        xmlName
        xmlNamespace
    ]
)
@traitValidators(
    UnsupportedProtocolDocument: {
        selector: "~> member :test(> document)"
        message: "Document types are not supported with restXml"
    }
)
@trait(selector: "service")
structure restXml with [HttpConfiguration] {
    /// Disables the serialization wrapping of error properties in an 'Error'
    /// XML element.
    @deprecated
    noErrorWrapping: Boolean
}

@private
list StringList {
    member: String
}

@length(min: 1)
@private
@uniqueItems
list ChecksumAlgorithmSet {
    member: ChecksumAlgorithm
}

// This enum should be in sync with the `HttpChecksumTrait` list.
@private
enum ChecksumAlgorithm {
    /// CRC64NVME
    CRC64NVME

    /// CRC32C
    CRC32C

    /// CRC32
    CRC32

    /// SHA1
    SHA1

    /// SHA256
    SHA256
}
