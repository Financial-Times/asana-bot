# Adding New Reports

You can add new reports to asana-bot by following these steps:

1. Set up the required desk on [asana](https://app.asana.com/)

2. Access `src/config/application-production.yml`

3. Find the `report` section, which should look something like this:

```
report:
  hostDomain: ft.com
  desks:
    Companies:
      projects:
        - id: 32896507461944
          name: Companies Topics
      groupTags: true
      premiumTags: []
    World:
      projects:
        - id: 28687437659749
          name: World Topics
      groupTags: true
      premiumTags: []
```

3. Add the appropriate report as a `desk` in the `report` section. We recently added the `Morning Conference` desk, which looks like this:

```
    Morning Conference:
      projects:
        - id: 1160158476341609
          name: Morning Conference
      groupTags: false
      premiumTags: []
```

Importantly, the `id` is the id for the list and not the id for the overview.
You can find the id in the URL for the list, eg:
`https://app.asana.com/0/1160158476341609/list`

4. Test with [asana reports](http://asanareports.ft.com/)
