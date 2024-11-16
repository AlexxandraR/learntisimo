using FluentValidation;
using FormInputModels.Account;

namespace FormInputModels.Validators
{
	public class LoginInputValidator : AbstractValidator<LoginInputModel>
	{
		public LoginInputValidator()
		{
			RuleFor(x => x.Email)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.EmailAddress().WithMessage("Neplatná emailová adresa!");
			RuleFor(x => x.Password)
				.NotEmpty().WithMessage("Toto pole je povinné!");
		}
	}
}