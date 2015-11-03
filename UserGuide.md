**NOTE:** this wiki is a work in progress.

**Contents**


# Introduction #

Infinitum is an extensible, robust framework enabling Android developers to quickly and efficiently create rich, domain-driven applications while facilitating the convention-over-configuration paradigm. Infinitum's core components include an ORM, DI, AOP and RESTful web service module.

This user guide is designed to provide explanation for some of the various framework features and, perhaps more important, how to use them.

For information on what's in progress and what's planned for Infinitum, check out the RoadMap.

# Configuration #

  * InfinitumCfgXml: XML framework configuration file.
  * InfinitumContext: stores framework configuration data read from `infinitum.cfg.xml` and creates `Session`s.
  * ContextFactory: access point for retrieving `InfinitumContext` instances.

# ORM #

The Infinitum ORM provides an object-oriented approach to application data persistence while factoring out the underlying datastore representation. It allows for the persistence of POJOs (Plain Old Java Objects) and offers an API for constructing and executing SQL-less database queries and object-oriented REST calls. The ORM's overall goal is to allow developers to spend more time focusing on their problem domain and core business logic and less time on underlying data-access and boilerplate code by providing a transparent persistence layer.

## Persistence Layer ##

The ORM's user-facing API is actually relatively small. All data transactions are made through a single `Session` interface. The `Session` is the primary persistence service and is used to construct and subsequently execute queries.

  * [Session](Session.md): Infinitum persistence service.
  * [Criteria](Criteria.md): object-oriented database queries.
  * [Criterion](Criterion.md): `Criteria` query restrictions.
  * [Conditions](Conditions.md): provides static factory methods for creating `Criterion`.
  * TypeAdapter: facilitates the mapping of datastore values to Java data types and vice versa.

## Domain Model Metadata ##

The Infinitum ORM can be provided with metadata for an application's domain model in order to configure some of its behavior. For example, a class can be marked transient or persistent, a model can be mapped to a specific table, or a field can be mapped to a specific column. Of course, following the principle of convention-over-configuration, this metadata is not required as Infinitum can infer it itself.

Metadata can come in two forms: XML map files and annotations. XML map files provide mapping information for domain classes while annotations provide the same information inline within the class.

### ORM Annotations ###

  * [Entity](Entity.md): indicates the persistence state of a model.
  * [Table](Table.md):  indicates the name of a table an entity is mapped to.
  * [Persistence](Persistence.md): indicates the persistence state of a field.
  * [Column](Column.md): indicates the name of a column a field is mapped to.
  * PrimaryKey: indicates if a field is a primary key.
  * [Unique](Unique.md): indicates that the field value must be unique to the table when being persisted to the database.
  * NotNull: indicates that the field may not contain a null value when being persisted to the database.
  * [Rest](Rest.md): indicates the name of a endpoint field a field is mapped to for a RESTful web service.
  * ManyToMany: indicates that the annotated field represents a many-to-many relationship with another persistent class.
  * ManyToOne: indicates that the annotated field represents a many-to-one relationship with another persistent class.
  * OneToMany: indicates that the annotated field represents a one-to-many relationship with another persistent class.
  * OneToOne: indicates that the annotated field represents a one-to-one relationship with another persistent class.

### Map Files ###

  * ImfXml: provides mapping information for a specific domain class.

# RESTful Client #

The Infinitum RESTful client provides an extensible API for communicating with a RESTful web service using objects.

  * RestfulClient: provides an API for communicating with a RESTful web service.
  * AuthenticationStrategy: describes how web service requests should be authenticated.
  * SharedSecretAuthentication: used for token-based/shared-secret authentication.
  * TokenGenerator: generates shared-secret tokens.
  * JsonDeserializer: tells Infinitum how to deserialize JSON responses into domain model instances.
  * XmlDeserializer: tells Infinitum how to deserialize XML responses into domain model instances.
  * [RestfulPairsTypeAdapter](http://code.google.com/p/infinitum-framework/wiki/TypeAdapter#RestfulPairsTypeAdapter): facilitates the mapping of Java data types to RESTful web service models through name-value pairs.
  * [RestfulJsonTypeAdapter](http://code.google.com/p/infinitum-framework/wiki/TypeAdapter#RestfulJsonTypeAdapter): facilitates the mapping of Java data types to RESTful web service models through JSON.
  * [RestfulXmlTypeAdapter](http://code.google.com/p/infinitum-framework/wiki/TypeAdapter#RestfulXmlTypeAdapter): facilitates the mapping of Java data types to RESTful web service models through XML.

# Dependency Injection #

  * BeanFactoryPostProcessor: enables an `InfinitumContext` to have its `BeanFactory` modified after it has been configured.
  * BeanPostProcessor: allows for beans to be modified after they have been initialized by the container.

## DI Annotations ##

  * [Component](Component.md): indicates that the annotated class is a framework component, meaning it is a candidate for auto-detection.
  * [Bean](Bean.md): specialization of the `Component` annotation indicating that the annotated class is a dependency-injection bean.
  * [Scope](Scope.md): indicates the scope of a bean.
  * [Autowired](Autowired.md): indicates that the annotated constructor, setter, or field is to be injected by the framework.
  * PostConstruct: indicates that the annotated method is to be invoked after dependency injection.

# AOP #

  * JoinPoint: provides advice with contextual information about a join point.
  * ProceedingJoinPoint: provides support for _around_ advice.

## AOP Annotations ##

  * [Aspect](Aspect.md): separates cross-cutting concerns from core application code by providing pointcut advice.
  * [Before](Before.md): indicates that the annotated advice is to be executed before a join point is invoked.
  * [After](After.md): indicates that the annotated advice is to be executed after a join point completes.
  * [Around](Around.md): indicates that the annotated advice is to be executed around a join point.

# Android Activities #

Infinitum provides a set of `Activity` extensions which take care of framework initialization, provide support for resource injection and event binding, and expose an InfinitumContext.

  * InfinitumActivity: `Activity` extension that provides support for resource injection and event binding.

## Activity Annotations ##

  * InjectLayout: indicates that the annotated `Activity` is to be injected with a layout by the framework.
  * InjectView: indicates that the given field is to be injected with a view by the framework.
  * InjectResource: indicates that the given field is to be injected with a resource by the framework.
  * Bind: indicates that the annotated `View` is to be bound to a callback method for a given event type.

# Logging #

In addition to its ORM and RESTful services, Infinitum offers an extremely lightweight logging framework which wraps Android's Logcat. This logging framework allows log statements to be made within application code but only asserted in debug environments. This means that logging code does not need any conditional statements or be removed altogether when preparing an application for release.

  * [Logger](Logger.md): prints log messages to Logcat but adheres to environment configuration.

# Getting Started #

Using Infinitum within your application is simple. The framework comes in the form of a JAR file, infinitum.jar. A [beta distribution](http://code.google.com/p/infinitum-framework/downloads/detail?name=infinitum-beta-1.1.zip) is currently available; however, this is not an official release and, as such, is not officially supported. The framework JAR should be placed in your application's libs directory. Alternatively, the Infinitum source can be imported into your workspace and then referenced from the application as an Android library.

Infinitum has three dependencies which must also be included in your application's build path in order to function properly; however, one of them is not needed if the [RestfulSession](https://code.google.com/p/infinitum-framework/wiki/Session#RestfulSession) is not being used. [Google Gson](http://code.google.com/p/google-gson) is required for the `RestfulSession` and, as such, only needs to be included if making use of it. [Dexmaker](http://code.google.com/p/dexmaker) and [Simple Framework](http://simple.sourceforge.net/) must be included for Infinitum to function. These JARs are included in the distribution mentioned above.

  * InstallationGuide: complete guide to setting up Infinitum.
  * [Javadoc](https://infinitum-framework.googlecode.com/svn/trunk/InfinitumFramework/doc/index.html): framework Javadoc API documentation.
  * ExampleApplication: simple notepad app that demonstrates the basics of Infinitum.