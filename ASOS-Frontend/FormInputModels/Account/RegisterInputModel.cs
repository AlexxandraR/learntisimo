using System.ComponentModel.DataAnnotations;
using ValidationAttributes;

namespace FormInputModels.Account
{
    public sealed class RegisterInputModel
    {
        [Required(ErrorMessage = "Email je povinný.")]
        [EmailAddress(ErrorMessage = "Neplatná emailová adresa.")]
		[RegularExpression(@"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$", ErrorMessage = "Neplatná emailová adresa.")]
		public string Email { get; set; }

        [Required(ErrorMessage = "Heslo je povinné.")]
		[PasswordComplexity]
		public string Password { get; set; }

        [Compare("Password", ErrorMessage = "Heslá sa nezhodujú.")]
        public string ConfirmPassword { get; set; }

        [Required(ErrorMessage = "Meno je povinné.")]
		public string FirstName { get; set; }

        [Required(ErrorMessage = "Priezvisko je povinné.")]
		public string LastName { get; set; }

        [Required(ErrorMessage = "Telefón je povinný.")]
		public string PhoneNumber { get; set; }
    }
}
