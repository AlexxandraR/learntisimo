
namespace FormInputModels.Other
{
	public sealed class CreateMeetingInputModel
	{
		public string Subject { get; set; }

		public int? Duration { get; set; }

		public DateTime? CombinedDateTime { get; set; }
	}
}
