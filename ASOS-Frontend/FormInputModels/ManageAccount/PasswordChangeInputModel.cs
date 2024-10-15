using System.ComponentModel.DataAnnotations;
using ValidationAttributes;

namespace FormInputModels.ManageAccount
{
    public sealed class PasswordChangeInputModel
    {
        [Required(ErrorMessage = "Toto pole je povinné! ")]
		public string Password { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné! ")]
		[PasswordComplexity]
		public string NewPassword { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné! ")]
        [Compare("NewPassword", ErrorMessage = "Heslá sa nezhodujú.")]
		public string ConfirmNewPassword { get; set; }
    }
}
