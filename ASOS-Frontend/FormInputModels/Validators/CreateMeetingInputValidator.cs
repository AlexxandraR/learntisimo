using FluentValidation;
using FormInputModels.Other;

namespace FormInputModels.Validators
{
	public class CreateMeetingInputValidator : AbstractValidator<CreateMeetingInputModel>
	{
		public CreateMeetingInputValidator()
		{
			RuleFor(x => x.Subject)
				.NotEmpty().WithMessage("Toto pole je povinné!");

			RuleFor(x => x.Duration)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.GreaterThan(0).WithMessage("Trvanie musí byť kladné číslo!");

			RuleFor(x => x.CombinedDateTime)
				.NotEmpty().WithMessage("Toto pole je povinné!")
				.Must(BeAFutureDate).WithMessage("Vyberte nadchádzajúci dátum a čas!");
		}

		private bool BeAFutureDate(DateTime? date)
		{
			return date.HasValue && date.Value > DateTime.Now;
		}
	}
}