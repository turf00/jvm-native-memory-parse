# Jvm Native Memory Parse

Simply converts the output from the JVM native memory dump, i.e.:

`jcmd <pid> VM.native_memory summary`

into a CSV file and adds columns showing MB.

## Usage

`java -jar <jar> <input-file> <output-file>`