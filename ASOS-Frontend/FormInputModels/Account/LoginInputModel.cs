using System.ComponentModel.DataAnnotations;

namespace FormInputModels.Account
{
    public sealed class LoginInputModel
    {
        [Required(ErrorMessage = "Toto pole je povinné!")]
        [EmailAddress(ErrorMessage = "Neplatná emailová adresa!")]
        public string Email { get; set; } = "";

        [Required(ErrorMessage = "Toto pole je povinné!")]
        [DataType(DataType.Password)]
		public string Password { get; set; } = "";
    }
}
