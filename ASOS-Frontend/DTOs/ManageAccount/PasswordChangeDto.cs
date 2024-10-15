using System.Text.Json.Serialization;

namespace DTOs.ManageAccount
{
	public class PasswordChangeDto
	{
		[JsonPropertyName("password")]
		public string Password { get; set; }

		[JsonPropertyName("newPassword")]
		public string NewPassword { get; set; }

		[JsonPropertyName("confirmNewPassword")]
		public string ConfirmNewPassword { get; set; }
	}
}
