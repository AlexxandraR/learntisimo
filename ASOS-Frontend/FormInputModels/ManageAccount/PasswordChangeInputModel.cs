
namespace FormInputModels.ManageAccount
{
    public sealed class PasswordChangeInputModel
    {
		public string Password { get; set; }

		public string NewPassword { get; set; }

		public string ConfirmNewPassword { get; set; }
    }
}
