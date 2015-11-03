**Contents**


# Introduction #

The Infinitum ORM provides an API for making object-oriented database queries using what are called `Criteria`, which completely removes the need for writing any SQL in your application. `Criteria` queries are used to query for a particular persistent class, and they allow for compile-time type checking, which means no casting is necessary. A `Criteria` is composed of [Criterion](Criterion.md), which act as restrictions or conditions on a query to refine its results.

# Constructing Criteria #

`Criteria` are created using a [Session](Session.md).

```
Criteria<Foo> criteria = session.createCriteria(Foo.class);
```

Once a `Criteria` has been created, `Criterion` can be added by calling `add(Criterion)`. Although they can be, `Criterion` typically are not instantiated directly, but rather are acquired using static factory methods in the [Conditions](Conditions.md) class:

```
session.createCriteria(Foo.class).add(Conditions.eq("mId", 42));
```

The above code adds a restriction to the `Criteria` query indicating that only results of type `Foo` whose ID (the `mId` field in the Java object) is equal to 42 will be returned. `Criteria`'s `add` method returns a reference to itself, allowing for method chaining:

```
session.createCriteria(Foo.class).add(Conditions.eq("mId", 42)).add(Conditions.like("mBar", "hello%"))
```

Of course, _no_ conditions can be added to a `Criteria` if querying for all records of a given type. For example, the below `Criteria` would be used to retrieve all instances of `Foo` from the database. This `Criteria` query could be executed as is.

```
session.createCriteria(Foo.class);
```

# Executing Queries #

Once a `Criteria` query has been constructed and the conditions have been added, it's time to execute it for a result. There are essentially two ways in which query results are retrieved, as a unique result or as a list.

If a query is to retrieve a unique result, `Criteria`'s `unique()` method should be invoked. This will return a single, unique query result or `null` if no such record exists. If there is no unique record, i.e. there are multiple results, an `InfinitumRuntimeException` will be thrown.

```
Foo foo = criteria.add(Conditions.eq("mId", 42)).unique();
```

To retrieve _all_ results, invoke `Criteria`'s `list()` method, which will return all query results as a list. This can be refined, however, by using `limit` and `offset`. For example, the following code will limit the query's result set to 10 records.

```
List<Foo> foos = criteria.add(Conditions.like("mBar", "hello%")).limit(10).list();
```

A result offset can also be added as such:

```
List<Foo> foos = criteria.add(Conditions.like("mBar", "hello%")).limit(10).offset(5).list();
```

The `count()` method can be used to retrieve the number of results for a query, which is analogous to `SELECT count(*)` in SQL.

```
long records = criteria.add(Conditions.like("mBar", "hello%")).count();
```