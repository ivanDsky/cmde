package ua.ivandsky.cmde

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CollaborativeMarkdownEditorApplication

fun main(args: Array<String>) {
	runApplication<CollaborativeMarkdownEditorApplication>(*args)
}
