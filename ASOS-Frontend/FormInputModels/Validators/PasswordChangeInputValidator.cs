using FluentValidation;
using FormInputModels.ManageAccount;

namespace FormInputModels.Validators
{
	public class PasswordChangeInputValidator : AbstractValidator<PasswordChangeInputModel>
	{
		public PasswordChangeInputValidator()
		{
			RuleFor(x => x.Password)
				.NotEmpty().WithMessage("Toto pole je povinné!");

			RuleFor(x => x.NewPassword)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.Matches(@"(?=.*[0-9])").WithMessage("Heslo musí obsahovať číslicu.")
				.Matches(@"(?=.*[a-z])").WithMessage("Heslo musí obsahovať malé písmeno.")
				.Matches(@"(?=.*[A-Z])").WithMessage("Heslo musí obsahovať veľké písmeno.")
				.Matches(@"(?=.*[!@#$%^&*()_+=])").WithMessage("Heslo musí obsahovať špeciálny znak.")
				.MinimumLength(8).WithMessage("Dĺžka hesla musí byť najmenej 8 znakov.");

			RuleFor(x => x.ConfirmNewPassword)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.Equal(x => x.NewPassword).WithMessage("Heslá sa nezhodujú.");
		}
	}
}