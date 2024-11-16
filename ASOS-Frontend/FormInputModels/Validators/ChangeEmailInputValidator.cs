using FluentValidation;
using FormInputModels.ManageAccount;

namespace FormInputModels.Validators
{
	public class ChangeEmailInputValidator : AbstractValidator<EmailChangeInputModel>
	{
		public ChangeEmailInputValidator()
		{
			RuleFor(x => x.Email)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.EmailAddress().WithMessage("Neplatná emailová adresa.")
				.Matches(@"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$")
				.WithMessage("Neplatná emailová adresa.");

			RuleFor(x => x.NewEmail)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.EmailAddress().WithMessage("Neplatná emailová adresa.")
				.Matches(@"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$")
				.WithMessage("Neplatná emailová adresa.")
				.Must((model, newEmail) => newEmail != model.Email)
				.WithMessage("Nový email sa nesmie zhodovať so starým.");

			RuleFor(x => x.Password)
				.NotEmpty().WithMessage("Toto pole je povinné!");
		}
	}
}