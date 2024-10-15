using System.Text.Json.Serialization;
using DTOs.Account;

namespace DTOs
{
    public class RequestDto
    {
        [JsonPropertyName("id")]
        public long? Id { get; set; }

        [JsonPropertyName("requestedAt")]
        public DateTime RequestedAt { get; set; }

        [JsonPropertyName("user")]
        public UserDto? User { get; set; }
    }
}
