using FluentValidation;
using FormInputModels.ManageAccount;

namespace FormInputModels.Validators
{
	public class ProfileInputValidator : AbstractValidator<ProfileInputModel>
	{
		public ProfileInputValidator()
		{
			RuleFor(x => x.Email)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.EmailAddress().WithMessage("Neplatná emailová adresa.");

			RuleFor(x => x.FirstName)
				.NotEmpty().WithMessage("Toto pole je povinné!");

			RuleFor(x => x.LastName)
				.NotEmpty().WithMessage("Toto pole je povinné!");

			RuleFor(x => x.PhoneNumber)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.Matches(@"^\+\d{12}$").WithMessage("Použite univerzálny formát: +XXXXXXXXXXXX");

			RuleFor(x => x.Description)
				.MaximumLength(500).WithMessage("Popis môže mať maximálne 500 znakov.")
				.When(x => !string.IsNullOrEmpty(x.Description));
		}
	}
}