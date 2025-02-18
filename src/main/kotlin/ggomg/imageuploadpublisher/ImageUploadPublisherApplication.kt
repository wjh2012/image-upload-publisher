package ggomg.imageuploadpublisher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ImageUploadPublisherApplication

fun main(args: Array<String>) {
    runApplication<ImageUploadPublisherApplication>(*args)
}
