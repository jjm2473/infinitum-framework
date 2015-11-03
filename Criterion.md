**Contents**


# Introduction #

`Criterion` are used to refine [Criteria](Criteria.md) query results by imposing restrictions or conditions. Since the `Criteria` API is entirely object-oriented, restrictions are placed on class fields rather than table columns, factoring out the underlying database schema altogether.

`Criterion` are typically not instantiated directly. Instead, they are acquired from one of the many static factory methods in [Conditions](Conditions.md). However, the `Criterion` interface allows for custom restriction implementations as well.