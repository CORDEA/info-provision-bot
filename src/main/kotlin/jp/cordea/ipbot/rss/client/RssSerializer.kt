package jp.cordea.ipbot.rss.client

import io.ktor.client.call.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.InputKind
import nl.adaptivity.xmlutil.serialization.UnknownChildHandler
import nl.adaptivity.xmlutil.serialization.XML

class RssSerializer : JsonSerializer {
    private val xml = XML {
        policy = DefaultXmlSerializationPolicy(
            false,
            unknownChildHandler = object : UnknownChildHandler {
                override fun invoke(
                    input: XmlReader,
                    inputKind: InputKind,
                    name: QName?,
                    candidates: Collection<Any>
                ) {
                    // Ignore unknown elements.
                }
            })
    }

    @ExperimentalSerializationApi
    override fun read(type: TypeInfo, body: Input): Any =
        xml.decodeFromString(serializer(type.reifiedType), body.readText())

    override fun write(data: Any, contentType: ContentType): OutgoingContent =
        TextContent(xml.encodeToString(data), contentType)
}
