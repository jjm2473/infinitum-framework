**Contents**


# Introduction #

The `Column` annotation is used to specify the table column a model entity's field maps to. It has a single attribute, `value`, which represents the name of the column. If the annotation is not provided, the field is mapped to a column with the same name as the field. If Android naming conventions are being followed, specifically member variable names are prefixed with a lowercase 'm' (i.e. `private int mFoo`), the prefix will be dropped. For example, a field named `mFoo` in a model class which does not have a `Column` annotation will be mapped to the column `foo`. Member variables that do not follow naming conventions will be mapped as expected, that is, a field named `foo` will be mapped to the column of the same name.

# `Column` Example #

As was mentioned, `Column` is an optional annotation and is only necessary when a specific column name, which does not match the field name, is desired. The example below maps the field `mBar` to the column `my_col`.

```
public class Foo {
    
    @Column("my_col")
    private int mBar;

}
```