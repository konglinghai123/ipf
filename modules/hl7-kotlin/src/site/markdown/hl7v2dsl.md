## HL7v2 Domain Specific Language

The [Kotlin]-based HL7v2 DSL provides a unique programming interface for handling HL7v2 messages. 
Its API aligns very closely with natural language and the syntax of HL7v2 as often seen in specifications and requirements, 
so that translation from the language of the "HL7 world" into the language of the "developer's world" becomes redundant.

### How it works

The `ipf-modules-hl7-kotlin` library adds the required methods to the [HAPI]
model classes. These methods are used "under the hood" by the actual DSL.

### Accessing HL7 messages

The DSL offers a position-based navigation of HL7 structures and fields. It's all valid Kotlin Syntax,
accomplished by operator overloading and metaclass programming, so you don't need a intermediate step that parses the
expressions (see the [Terser] class in HAPI).

#### Accessing groups and segments

Groups and segments can be accessed by name like an object property.

```kotlin
    import ca.uhn.hl7v2.model.Message

    val message = parser.parse(msgTxt)
    val msh   = message["MSH"]
    val group = message["PATIENT_RESULT"]
    val pid   = message["PATIENT_RESULT"]["PATIENT"]["PID"]
```

Details are described [here][hl7v2dslStructures].

#### Accessing fields and field values

Obtaining fields is similar to obtaining groups and segments except that fields are often referred to by number instead
of by name. Fields are accessed like an array field. Components in a composite field are accessed like a two-dimensional array.
Subcomponents are accessed like a three-dimensional array.

```kotlin
    val messageType = message["MSH"][9][1].value
    val street = message["PATIENT_RESULT"]["PATIENT"]["PID"][11][1][1].value
```

Details are described [here][hl7v2dslFields].


#### Repetitions

Groups, segments and fields may be repeatable. Parentheses like with regular method calls are used in order to obtain a
certain element of a repeating structure.

```kotlin
    val group = message["PATIENT_RESULT"](0)["PATIENT"]
    val nk1   = group["NK1"](1)
```

Details are described [here][hl7v2dslRepetitions].

#### Smart navigation

Accessing HL7 messages as described above usually requires knowledge about the specified message structure,
which is often not visible by looking at the printed message.
To make things worse, the internal structure changes between HL7 versions. In more recent versions, primitive types are
sometimes replaced with composite types having the so far used primitive as first component.
This appears to be backwards compatible on printed messages, but requires different DSL expressions when obtaining field values.

Smart navigation resolves these problems by assuming reasonable defaults when repetitions or component operators are omitted

```kotlin
    assertEquals(group["NK1"](0)[2][1][1].value, group["NK1"][2].value)
```

Details are described [here][hl7v2dslSmart].

#### Iterative functions

As HL7 messages are compound structures, it should be possible to traverse them. Thus, the HL7 DSL implements iterators for
HL7 messages and groups. Due to their nested structures, iteration is implemented as a depth first traversal over all
non-empty substructures, i.e. non-empty groups and segments.

```kotlin
    import ca.uhn.hl7v2.model.Message

    val hasGroups = message.asIterable().any { it is Group }
    val allStructureNames = message.asIterable().map { it.name }
```

Details are described [here][hl7v2dslIteration].

### Creating HL7 messages (and their internal structures)

New messages can be created from scratch by just specifying HL7 event type, trigger event and version, i.e. it is not required to know
about the object type to be instantiated.
The message header fields are populated with the event type, trigger event, version, the current time as message date, and the common separators.

Just as creating a message, segments and fields can be created without actually knowing their respective type.

Details are described [here][hl7v2dslcreating].

### Manipulating HL7 messages

Message manipulation is as straightforward as read access. You navigate to a segment or field and assign it a new object or String value.

Details are described [here][hl7v2dslManipulation].


### Parsing and Rendering HL7 messages

IPF does not change or extend [HAPI] on how to parse or render HL7 message.


[HAPI]: https://hapifhir.github.io/hapi-hl7v2/
[Kotlin]: https://kotlinlang.org
[Terser]: https://hapifhir.github.io/hapi-hl7v2//base/apidocs/ca/uhn/hl7v2/util/Terser.html
[hl7v2dslStructures]: hl7v2dslStructures.html
[hl7v2dslFields]: hl7v2dslFields.html
[hl7v2dslRepetitions]: hl7v2dslRepetitions.html
[hl7v2dslSmart]: hl7v2dslSmartNavigation.html
[hl7v2dslIteration]: hl7v2dslIteration.html
[hl7v2dslcreating]: hl7v2dslcreating.html
[hl7v2dslManipulation]: hl7v2dslManipulation.html