**Contents**


# Introduction #

The `Table` annotation defines the database table an entity is mapped to. It has a single attribute, `value`, which represents the name of the table. If the annotation is not provided, the entity is mapped to a table with the same name as the class. For example, a persistent class `FooBar` which does not have a `Table` annotation will be mapped to the table `foobar`.

# `Table` Example #

As described above, the `Table` annotation is not required; however, it can be used to explicitly define the table a model class maps to. Below is an example which uses the `Table` annotation to map the class `Foo` to the table `my_table`.

```
@Table("my_table")
public class Foo {
    // ...
}
```