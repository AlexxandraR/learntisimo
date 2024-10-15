using System.ComponentModel.DataAnnotations;

namespace FormInputModels
{
	public sealed class CreateCourseInputModel
	{
		[Required(ErrorMessage = "Toto pole je povinné!")]
		public string Subject { get; set; }

		[Required(ErrorMessage = "Toto pole je povinné!")]
		[Range(0.01, double.MaxValue, ErrorMessage = "Cena musí byť kladné číslo!")]
		public double? Price { get; set; }

		[Required(ErrorMessage = "Toto pole je povinné!")]
		public string Room { get; set; }
	}
}
