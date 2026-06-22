
<a href="https://www.craniocreations.it/prodotto/mesos">
    <img alt="logo" src="https://github.com/user-attachments/assets/3678bab9-fba5-4c10-9c79-79911825b421"/>
</a>
<h1 align="center">Software Engineering Final Project</h1>
<h3 align="center">085923 - PROVA FINALE (INGEGNERIA DEL SOFTWARE) A.A. 2025/2026</h3>
<p align="center">
    AM43 Group
</p>
<p align="center">
    <a href="https://github.com/peter-ss1">Pietro Cimino</a> •
    <a href="https://github.com/michelcostantini">Michel Costantini</a> •
    <a href="https://github.com/genna0506">Lorenzo Gennari</a> •
    <a href="https://github.com/paolooferrarii">Paolo Ferrari</a>
</p>

## Overview

The project implements the digital version of the <a href="https://www.craniocreations.it/prodotto/mesos"> Mesos </a> board game, produced by <a href="https://www.craniocreations.it/"> Cranio Creations </a>. It utilizes the MVC pattern, distributed over a client-server architecture. The implementation allows multiple concurrent matches and stores ranking data in an auxiliary database. Disconnections and crashes are handled both client- and server-side, improving user experience. The client application offers a Text-based User Interface (TUI) as well as a Graphical User Interface (GUI).

## Implemented Features
The project follows the provided [Requirements](https://github.com/user-attachments/files/29117144/requirements.pdf), implementing all four advanced features.


<table align="center" width="100%">
<tr>
<td valign="top" width="50%">
<center>

<h3 align="center">Base Features</h3>

| Feature | Status |
| :---: | :---: |
| Complete rules | ✔️ |
| Socket protocol | ✔️ |
| RMI protocol | ✔️ |
| TUI | ✔️ |
| GUI | ✔️ |

</center>
</td>
<td valign="top" width="50%">
<center>

<h3 align="center">Advanced Features</h3>

| Feature | Status |
| :---: | :---: |
| Database Ranking | ✔️ |
| Multiple Matches | ✔️ |
| Disconnection Resilience | ✔️ |
| Server Persistence | ✔️ |

</center>
</td>
</tr>
</table>

## Documentation

The project documentation can be found in the `/deliverables` directory, which contains:

* **High-Level UML Diagrams**: Application architecture diagrams showing the general design.
* **Detailed UML Diagrams**: Complete diagrams showing all aspects of the application, generated directly from the source code.
* **Network Protocol Documentation**: Sequence diagrams showing the main processes of the communication protocol between the client and the server.
* **Javadoc Documentation**: Complete comment documentation.

### Used Tools

<td valign="top">
<center>

| Tool | Description |
| :---: | :---: |
| Maven | Project management and Java build automation |
| JavaFX | GUI framework |
| JUnit | Framework used for automated unit testing |
| Jackson | JSON parser for data serialization |
| HikariCP | JDBC connection pool for database management |

</center>
</td>


## User Manual

### 1. Generating JARs via IntelliJ IDEA (Maven required)

1. Open the project in **IntelliJ IDEA**.
2. Open the **Maven Tool Window** located on the right-hand sidebar (or go to *View -> Tool Windows -> Maven*).
3. Expand your project lifecycle tree.
4. Double-click **`clean`** to clear any previous build artifacts.
5. Double-click **`package`** to compile the code and execute test suites.
6. Once the build finishes successfully, look into your project directory. You will find the generated `.jar` files (`server.jar` and `client.jar`) inside the newly created **`/target`** folder.

### 2. Running Application from Terminal

Once you are in your terminal, navigate to the directory where your `.jar` files are stored and run one of the following commands to execute the corresponding application. **Please note that Java 25 is required to run these files.**

```bash
java -jar server.jar
java -jar client.jar

```

Note: if you are planning to use the Text-based User Interface (TUI), make sure to zoom in or out as needed to visualize the proper layout. If you are on Windows, your terminal may not default to the correct encoding. For optimal graphical experience, run the following commands based on your terminal:

#### Windows PowerShell

Run this command inside your session before launching the JAR to force PowerShell to output Unicode characters correctly:

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
java -jar client.jar

```

#### Windows Command Prompt (cmd)

Change the active code page to UTF-8 (`65001`) before executing the application:

```cmd
chcp 65001
java -jar client.jar

```

## Gallery

### GUI

### TUI
