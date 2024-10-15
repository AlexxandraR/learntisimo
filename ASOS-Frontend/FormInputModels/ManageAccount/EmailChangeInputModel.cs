using System.ComponentModel.DataAnnotations;

namespace FormInputModels.ManageAccount
{
    public sealed class EmailChangeInputModel
    {
        [Required(ErrorMessage = "Toto pole je povinné! ")]
        [EmailAddress(ErrorMessage = "Neplatná emailová adresa.")]
		[RegularExpression(@"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$", ErrorMessage = "Neplatná emailová adresa.")]
		public string Email { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné! ")]
        [EmailAddress(ErrorMessage = "Neplatná emailová adresa.")]
		[RegularExpression(@"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$", ErrorMessage = "Neplatná emailová adresa.")]
		public string NewEmail { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné! ")]
		public string Password { get; set; }
    }
}
