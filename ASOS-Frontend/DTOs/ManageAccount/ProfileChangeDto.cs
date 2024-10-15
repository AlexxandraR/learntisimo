using System.Text.Json.Serialization;

namespace DTOs.ManageAccount
{
	public class ProfileChangeDto
	{
		[JsonPropertyName("degree")]
		public string? Degree { get; set; }

		[JsonPropertyName("email")]
		public string Email { get; set; }

		[JsonPropertyName("firstName")]
		public string FirstName { get; set; }

		[JsonPropertyName("lastName")]
		public string LastName { get; set; }

		[JsonPropertyName("phoneNumber")]
		public string PhoneNumber { get; set; }

		[JsonPropertyName("description")]
		public string? Description { get; set; }
	}
}
