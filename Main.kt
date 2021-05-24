package svcs

import java.io.File

object Values {
    val configFile = getFile("vcs/config.txt")
    val indexFile = getFile("vcs/index.txt")
    val logFile = getFile("vcs/log.txt")
    val log = logFile.readLines().toLogList()
    val currentFiles = indexFile.readText().toFilesList()
    val currentUser = configFile.readText()
    var filesChanged = false
}

fun main(args: Array<String>) {
    when (args.firstOrNull()) {
        null -> printHelpPage()
        "--help" -> printHelpPage()
        "config" -> config(args)
        "add" -> add(args)
        "log" -> log()
        "commit" -> commit(args)
        "checkout" -> checkout(args)
        else -> println("'${args[0]}' is not a SVCS command.")
    }
}

class Commit(val id: Int, val author: String, val comment: String) {
    override fun toString(): String = "[$id], [$author], [$comment]"
}

fun config(args: Array<String>) {
    if (args.size == 1) {
        if (Values.currentUser.isEmpty()) println("Please, tell me who you are.") else printUsername(Values.currentUser)
    } else {
        Values.configFile.writeText(args[1])

        printUsername(args[1])
    }
}

fun add(args: Array<String>) {
    if (args.size == 1) {
        if (Values.currentFiles.isEmpty()) println("Add a file to the index.") else printTrackedFileNames()
    } else {
        if (File(args[1]).exists()) {
            val newFiles = Values.currentFiles.toSet().plus(args[1]).toList()

            Values.indexFile.writeText(newFiles.toString())

            println("The File '${args[1]}' is tracked.")
        } else {
            println("Can't found '${args[1]}'.")
        }
    }
}

fun log() = if (Values.log.isEmpty()) println("No commits yet.") else printLog()

fun commit(args: Array<String>) {
    if (args.size == 1) println("Message was not passed.") else {
        if (Values.log.isEmpty() || Values.filesChanged) addCommit(args) else println("Nothing to commit.")
    }
}

fun checkout(args: Array<String>) {
    if (args.size == 1) println("Commit id was not passed.") else {
        val file = File("vcs/commits/${args[1]}")

        if (file.exists()) {
            Values.currentFiles.forEach { File("$file/$it").copyTo(File(it), true) }

            println("Switched to commit ${args[1]}.")
        } else {
            println("Commit does not exist.")
        }
    }
}

fun fileChanged(fileName: String): Boolean {
    val file = File("vcs/commits/${"%06d".format(getId())}/$fileName")

    return if (file.exists()) File(fileName).readText().hashCode() != file.readText().hashCode() else false
}

fun addCommit(args: Array<String>) {
    val newId = getId() + 1
    val newLog = Values.log.reversed().plus(Commit(newId, Values.currentUser, args[1])).reversed()

    Values.currentFiles.forEach { File(it).copyTo(File("vcs/commits/${"%06d".format(newId)}/$it"), true) }

    Values.logFile.writeText(newLog.toLogString())

    println("Changes are committed.")
}

fun getFile(pathName: String): File {
    val vcs = File("vcs")
    val file = File(pathName)

    if (!vcs.exists()) vcs.mkdir()

    if (!file.exists()) file.createNewFile()

    return file
}

fun getId(): Int = if (Values.log.isEmpty()) 0 else Values.log.first().id

fun printUsername(username: String) = println("The username is $username.")

fun printTrackedFileNames() {
    println("Tracked files:")

    Values.currentFiles.forEach { println(it) }
}

fun printLog() {
    Values.log.forEach {
        println("commit ${"%06d".format(it.id)}\nAuthor: ${it.author}\n${it.comment}\n")
    }
}

fun printHelpPage() {
    println("These are SVCS commands:\n" +
            "config     Get and set a username.\n" +
            "add        Add a file to the index.\n" +
            "log        Show commit logs.\n" +
            "commit     Save changes.\n" +
            "checkout   Restore a file.")
}

fun String.toFilesList(): List<String> {
    if (this.isEmpty()) return emptyList()

    val list = mutableListOf<String>()

    this.substringAfter('[').substringBefore(']').split(", ").forEach {
        if (fileChanged(it)) Values.filesChanged = true

        list.add(it)
    }

    return list.toList()
}

fun List<String>.toLogList(): List<Commit> {
    if (this.isEmpty()) return emptyList()

    val list = mutableListOf<Commit>()

    this.forEach {
        if (it.isNotEmpty()) {
            val (id, user, comment) = it.substringAfter('[').substringBeforeLast(']').split("], [")

            list.add(Commit(id.toInt(), user, comment))
        }
    }

    return list
}

fun List<Commit>.toLogString(): String {
    var string = ""

    this.forEach { string = string.plus("$it\n") }

    return string.substringBeforeLast("\n")
}