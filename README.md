### Infrastructure up
`cd infrastructure/database`  
`docker-compose up`

### Application start
Run the application `sbt run` -> runs the application on `9000` port

### Troubleshooting

#### Windows

1. Virtualization error on `docker compose up`
> Solution A
> Enable Hyper-V on Windows machine in BIOS settings. As for ASUS motherboard
> there is an Advanced -> CPU section
 
> Solution B
> Poweshell Administrator access -> `dism.exe /Online /Enable-Feature:Microsoft-Hyper-V /All`

> Solution C 
> Poweshell Administrator access -> bcdedit /set hypervisorlaunchtype auto
