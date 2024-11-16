using FluentValidation;
using FormInputModels.Other;

namespace FormInputModels.Validators
{
	public class CreateCourseInputValidator : AbstractValidator<CreateCourseInputModel>
	{
		public CreateCourseInputValidator()
		{
			RuleFor(x => x.Subject)
						.NotEmpty().WithMessage("Toto pole je povinné!");

			RuleFor(x => x.Price)
						.NotEmpty().WithMessage("Toto pole je povinné!")
						.GreaterThan(0).WithMessage("Cena musí byť kladné číslo!");

			RuleFor(x => x.Room)
						.NotEmpty().WithMessage("Toto pole je povinné!");
		}
	}
}