---
title: "Custom Types"
---

Custom types let you create named drop categories that map back to one of TradingCards' built-in mob groups.

## Creating a Custom Type

```text
/cards create type <typeId> <defaultType>
```

`defaultType` must be one of the built-in groups:

- `boss`
- `hostile`
- `neutral`
- `passive`
- `all`

## Editing a Custom Type

```text
/cards edit type <typeId> <TYPE|DISPLAY_NAME> <value>
```

## When to Use Custom Types

- Separate one set of mob drops from another without changing the underlying built-in categories.
- Give server-specific naming to a drop group.
- Keep your cards readable when you build themed sets or event-specific drop pools.
