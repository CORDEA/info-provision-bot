package client

import kotlinx.serialization.Serializable

@Serializable
class StreamRulesRequest(
    val add: List<StreamRuleRequest>
)

@Serializable
class StreamRuleRequest(
    val value: String
)
