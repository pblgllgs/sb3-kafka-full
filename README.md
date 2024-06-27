# Spring Application

## Kafka cluster in Windows

## Storage

```bash
cd kafka_directory

.\bin\windows\kafka-storage.bat random-uuid

<UUID>

.\bin\windows\kafka-storage.bat format -t <UUID> -c config/kraft/server-1.properties
.\bin\windows\kafka-storage.bat format -t <UUID> -c config/kraft/server-2.properties
.\bin\windows\kafka-storage.bat format -t <UUID> -c config/kraft/server-3.properties
```

## START

```bash
cd kafka_directory

.\bin\windows\kafka-server-start.bat .\config\kraft\server-1.properties
.\bin\windows\kafka-server-start.bat .\config\kraft\server-2.properties
.\bin\windows\kafka-server-start.bat .\config\kraft\server-3.properties
```


## STOP

```bash
cd kafka_directory

.\bin\windows\kafka-server-stop.bat
```

