using System.Text.Json.Serialization;

namespace DTOs.ManageAccount
{
	public class EmailChangeDto
	{
		[JsonPropertyName("email")]
		public string Email { get; set; }

		[JsonPropertyName("newEmail")]
		public string NewEmail { get; set; }

		[JsonPropertyName("password")]
		public string Password { get; set; }
	}
}
