using System.Text.Json.Serialization;

namespace DTOs.Account
{
	public class RegisterDto
	{
		[JsonPropertyName("email")]
		public string Email { get; set; }

		[JsonPropertyName("password")]
		public string Password { get; set; }

		[JsonIgnore]
		public string ConfirmPassword { get; set; }

		[JsonPropertyName("firstName")]
		public string FirstName { get; set; }

		[JsonPropertyName("lastName")]
		public string LastName { get; set; }

		[JsonPropertyName("phoneNumber")]
		public string PhoneNumber { get; set; }
	}
}
