using System.Text.Json.Serialization;
using DTOs.Account;

namespace DTOs
{
    public class RequestDto
    {
        [JsonPropertyName("id")]
        public long? Id { get; set; }

        [JsonPropertyName("dateTime")]
        public DateTime RequestedAt { get; set; }

        [JsonPropertyName("teacher")]
        public UserDto? User { get; set; }

		[JsonPropertyName("status")]
		public string? Status { get; set; }
	}
}
