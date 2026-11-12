### Infrastructure up

`cd infrastructure/database`  
`docker-compose up`

### Application start

Run the application `sbt run` -> runs the application on `9000` port

### Run tests

`sbt test`
`sbt testQuick`
`sbt testOnly services.migrations.MigrationsSpec`

### Troubleshooting

#### No sources attached

Run (sbt console)

`sbt updateClassifiers`
`sbt updateSbtClassifiers`

* Might be helpful to invalidate idea indexes after that

#### Windows

1. Virtualization error on `docker compose up`

> Solution A
> Enable Hyper-V on Windows machine in BIOS settings. As for ASUS motherboard
> there is an Advanced -> CPU section

> Solution B
> Poweshell Administrator access -> `dism.exe /Online /Enable-Feature:Microsoft-Hyper-V /All`

> Solution C
> Poweshell Administrator access -> bcdedit /set hypervisorlaunchtype auto


### License

Released under the [MIT License](LICENSE). Copyright (c) 2022-2023 Aleksei Spirin.

Third-party components keep their own licenses: the sbt launcher bundled in `sbt-dist/`
comes from the [sbt project](https://github.com/sbt/launcher) and is licensed under
the Apache License 2.0 (see [`sbt-dist/LICENSE`](sbt-dist/LICENSE) and
[`sbt-dist/NOTICE`](sbt-dist/NOTICE)).

### History note

This history was rewritten for the public release to remove personal and third-party
images; the product stub images were replaced with generated placeholders. Each commit
keeps its original author and date, and an `Original-commit:` trailer records its hash
in the original repository.
