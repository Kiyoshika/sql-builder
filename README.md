# SQL Builder
This library is aimed at API developers that use "raw SQL" for their backend rather than an ORM.

The process of writing raw or even templated queries can be very tedious and get very messy very quickly, especially for more complicated or dynamically generated queries.

This library provides builder patterns for various SQL queries such as select statements, common table expressions, joins, filters, etc.

## Architecture
This library has a `SQLContext` that all builders take in via their constructor. This context mainly defines the SQL dialect or any other "global" options when generating queries. Use `EDialect.STANDARD` for an "unopinionated" output that's not specific to a database.

## Documentation Contents
* [Building Select Statements](#building-select-statements)
  * [Select Everything](#select-everything)
  * [Select Columns](#select-columns)
  * [Adding Limits](#adding-limits)
  * [Simple Filters](#simple-filters)
  * [List Filters](#list-filters)
  * [Complex Filters](#complex-filters)

## Building Select Statements
### Select Everything
The simplest case - selecting everything from a table. Honestly, for simple cases like these, this library is not really needed. But nevertheless, here's how you do it:

```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        .build();
```

This will generate the following SQL:
```sql
SELECT * FROM sample_table;
```

You can also alias the table with `fromTableWithAlias()`:
```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTableWithAlias("sample_table", "st")
        .build();
```

Which will generate the following SQL:
```sql
SELECT * FROM sample_table AS st;
```

Note that the order doesn't necessarily matter; you can add the call to `fromTable` before `selectAll`, so order it however you want.

### Adding Limits
Add limits with the `limit(int)` method:
```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        .limit(100)
        .build();
```

Which will generate the following SQL:
```sql
SELECT * FROM sample_table LIMIT 100;
```

### Select Columns
There are a couple ways to select specific columns.

Method 1: Add numerous `select()` calls

Method 2: Pass `Arrays.asList(...)` to one `select()` call

Below are examples using both approaches:
```java
SQLContext context = new SQLContext(EDialect.STANDARD);

// Method 1
String query = new SelectBuilder(context)
        .select("col1")
        // optionally provide aliases for columns
        .selectWithAlias("col2", "alias")
        .select("col3")
        .fromTable("sample_table")
        .build();

// Method 2
query = new SelectBuilder(context)
        // if using asList, aliases must be done manually
        .select(Arrays.asList("col1", "col2 AS alias", "col3"))
        .fromTable("sample_table")
        .build();
```

Which will generate the following SQL (unformatted):
```sql
SELECT
    col1,
    col2 AS alias,
    col3
FROM
    sample_table;
```

### Simple Filters
Apply simple filters with the `filter()` method which takes a column name, condition, generic value, and whether or not the value(s) are quoted.

Quoted values are values wrapped in single quotes (e.g., text/varchar). Anything that is quoted is sanitized by escaping any isolated single quotes within the provided string to reduce the risk of SQL injection.

By default, adding multiple filters defaults to `AND` conjunction, but can be changed by attaching `and()` or `or()` at the end of a filter.

Below are some examples of simple filters:

```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        .filter("col1", EFilterCondition.EQUAL, 10, false)
        .filter("col2", EFilterCondition.LIKE, "Sam's%", true)
        .build();
```

Which generates the following SQL (unformatted):
```sql
SELECT * FROM sample_table
WHERE
    col1 = 10
AND col2 LIKE 'Sam''s%';
```

Note the single quote in `Sam's` was escaped with `Sam''s`.

We applied two filters, which be default uses the `AND` conjunction, but we can change this:
```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        .filter("col1", EFilterCondition.EQUAL, 10, false)
        .or()
        .filter("col2", EFilterCondition.LIKE, "Sam's%", true)
        .build();
```
Which generates the following SQL (unformatted):
```sql
SELECT * FROM sample_table
WHERE
    col1 = 10
OR  col2 LIKE 'Sam''s%';
```

### List Filters
This also supports `IN` and `NOT IN`. You can either pass an iterable or a non-iterable.

For non-iterables, it will look like, e.g., `IN (10)`.

For iterables, it will look like, e.g., `IN (10,20,30)`.

If using quoted values, then ALL values in the list are quoted, e.g., `IN ('10','20','30')`.

Below are some examples:

```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        // non-iterable. not sure why you would do this,
        // but it will work.
        .filter("col1", EFilterCondition.LIKE, 10, false)
        .build();
```

Which produces the following SQL:
```sql
SELECT * FROM sample_table WHERE col1 IN (10);
```

Using an iterable:
```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        .filter("col1", EFilterCondition.LIKE, Arrays.asList(10, 20, 30), false)
        .build();
```

Which generates the following SQL:
```sql
SELECT * FROM sample_table WHERE col1 IN (10,20,30);
```

This should work for anything that is derived from `Iterable<?>`

### Complex Filters
Sometimes the simple filters shown above is just not enough.

The `FilterGroupBuilder` creates a collection of conditions as a quotient that can be used to build more complex filters.

See the following example:

```java
SQLContext context = new SQLContext(EDialect.STANDARD);

String query = new SelectBuilder(context)
        .selectAll()
        .fromTable("sample_table")
        .filter("col1", EFilterCondition.EQUAL, 10, false)
        .filter(new FilterGroupBuilder()
                .addFilter("col2", EFilterCondition.EQUAL, 20, false)
                .addFilter("col3", EFilterCondition.LESS_THAN, 30, false)
                .anyOf())
        .build();
```

Which generates the following SQL (unformatted):
```sql
SELECT * FROM sample_table
WHERE
    col1 = 10
AND (
        col2 = 20
    OR  col3 < 30
);
```

The `FilterGroupBuilder` uses `anyOf()` to convert all conditions to `OR` and `allOf()` to convert all conditions to `AND` (which is the default.)

The syntax of `addFilter` is the same as `filter` for simplicity.