### Requirements

* Java 11
* Scala 2.13.8

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

#### Run sbt with specified java version

`sbt test --java-home "C:\Program Files\Java\jdk-11"`

#### Windows

1. Virtualization error on `docker compose up`

> Solution A
> Enable Hyper-V on Windows machine in BIOS settings. As for ASUS motherboard
> there is an Advanced -> CPU section

> Solution B
> Poweshell Administrator access -> `dism.exe /Online /Enable-Feature:Microsoft-Hyper-V /All`

> Solution C
> Poweshell Administrator access -> bcdedit /set hypervisorlaunchtype auto