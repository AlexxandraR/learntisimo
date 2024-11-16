using FluentValidation;
using FormInputModels.Account;

namespace FormInputModels.Validators
{
	public class RegisterInputModelValidator : AbstractValidator<RegisterInputModel>
	{
		public RegisterInputModelValidator()
		{
			RuleFor(x => x.Email)
				.NotEmpty().WithMessage("Email je povinný.")
				.EmailAddress().WithMessage("Neplatná emailová adresa.")
				.Matches(@"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$")
				.WithMessage("Neplatná emailová adresa.");

			RuleFor(x => x.Password)
				.NotEmpty().WithMessage("Heslo je povinné.")
				.Matches(@"[0-9]").WithMessage("Heslo musí obsahovať číslicu.")
				.Matches(@"[a-z]").WithMessage("Heslo musí obsahovať malé písmeno.")
				.Matches(@"[A-Z]").WithMessage("Heslo musí obsahovať veľké písmeno.")
				.Matches(@"[!@#$%^&*()_+=]").WithMessage("Heslo musí obsahovať špeciálny znak.")
				.MinimumLength(8).WithMessage("Dĺžka hesla musí byť najmenej 8 znakov.");

			RuleFor(x => x.ConfirmPassword)
				.NotEmpty().WithMessage("Potvrdenie hesla je povinné.")
				.Equal(x => x.Password).WithMessage("Heslá sa nezhodujú.");

			RuleFor(x => x.FirstName)
				.NotEmpty().WithMessage("Meno je povinné.");

			RuleFor(x => x.LastName)
				.NotEmpty().WithMessage("Priezvisko je povinné.");

			RuleFor(x => x.PhoneNumber)
				.NotEmpty().WithMessage("Telefón je povinný.")
				.Matches(@"^\+\d{12}$").WithMessage("Použite univerzálny formát: +XXXXXXXXXXXX");
		}
	}
}