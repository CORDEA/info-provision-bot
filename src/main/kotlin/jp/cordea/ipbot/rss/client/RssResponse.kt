package jp.cordea.ipbot.rss.client

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("rss", "", "")
data class RssResponse(
    @XmlElement(true)
    @XmlSerialName("channel", "", "")
    val channel: RssChannelResponse,
)

@Serializable
data class RssChannelResponse(
    @XmlElement(true)
    val title: String,
    @XmlElement(true)
    val link: String,
    @XmlElement(true)
    @XmlSerialName("item", "", "")
    val item: List<RssItemResponse>
)

@Serializable
data class RssItemResponse(
    @XmlElement(true)
    val title: String,
    @XmlElement(true)
    val pubDate: String,
    @XmlElement(true)
    val link: String,
    @XmlElement(true)
    val guid: String
)
