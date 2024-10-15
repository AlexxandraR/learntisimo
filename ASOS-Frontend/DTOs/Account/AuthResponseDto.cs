using System.Text.Json.Serialization;

namespace DTOs.Account
{
    public class AuthResponseDto
    {
        [JsonPropertyName("access_token")]
        public string AccessToken { get; set; }

        [JsonPropertyName("refresh_token")]
        public string RefreshToken { get; set; }
    }
}
