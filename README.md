# LegoCBM
## Why

Test `EV3Dev-lang-java`.

## Prerequisites

The Prerequisites to use this project are:

- Your MINDSTORMS Brick needs to have installed latest `Stable Debian Jessie` version. http://www.ev3dev.org/downloads/
- Your MINDSTORMS Brick needs to have installed `Oracle Java JRE 8`. http://ev3dev-lang-java.github.io/docs/support/getting_started/brick.html 
- Your MINDSTORMS Brick needs to be connected to the same LAN than your laptop. http://www.ev3dev.org/docs/getting-started/#step-5-set-up-a-network-connection

Once you have all steps done, continue with the next section.

## Getting Started

This repository stores a template project about `EV3Dev-lang-java`. 
Once you download in your computer the project, 
open your favourite Java IDE ( [Eclipse](https://eclipse.org/home/index.php) or [IntelliJ](https://www.jetbrains.com/idea/))
to import this [Gradle](https://gradle.org/) project. The project includes latest dependencies and
an example ready to be deployed on your Robot using the `core` library from `EV3Dev-lang-java`.

The project includes some tasks to reduce the time to deploy on your robot.

Review the IP of your Brick and update the file `./gradle/deploy.gradle`:

```
remotes {
    ev3dev {
        host = '192.168.1.180'
        user = 'robot'
        password = 'maker'
    }
}
```

The tasks associated to deploy on your Robot are:

- testConnection (Test the connection with your Brick)
- deploy (The project deliver a FatJar to your Brick)
- remoteRun (Execute a Jar deployed on your Brick)
- deployAndRun (Deploy & Execute from your Computer the program that you configured on the file: MANIFEST.MF)

You can use the Java IDE to launch the task or execute them from the terminal

```
./gradlew deployAndRun
```

## Other
- If alternatively you have connected the pi to a screen monitor via HDMI press Ctrl+Alt+F6 at the end of the boot sequence.
- Get ip on Raspberry Pi : ip -a



trouver l'ip: arp -a