**Contents**


# Introduction #

The `Unique` annotation is used to indicate if an entity class's field is unique to the table when being persisted to the database. More concisely, this annotation is used to define a unique constraint on a column. This has no effect on a field that is marked transient.

# `Unique` Example #

The following example shows how the field `mBar` is made unique.

```
public class Foo {

    @Unique
    private int mBar;

    public int getBar() {
        return mBar;
    }

    public void setBar(int bar) {
        mBar = bar;
    }

}
```