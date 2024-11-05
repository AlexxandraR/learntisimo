using System.ComponentModel.DataAnnotations;
using ValidationAttributes;

namespace FormInputModels
{
	public sealed class CreateMeetingInputModel
	{
		[Required(ErrorMessage = "Toto pole je povinné!")]
		public string Subject { get; set; }

		[Required(ErrorMessage = "Toto pole je povinné!")]
		[Range(1, int.MaxValue, ErrorMessage = "Trvanie musí byť kladné číslo!")]
		public int? Duration { get; set; }

		[Required(ErrorMessage = "Toto pole je povinné!")]
		[FutureDateTimeOnly]
		public DateTime? CombinedDateTime { get; set; }
	}
}
