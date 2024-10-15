using System.Text.Json.Serialization;
using DTOs.Account;

namespace DTOs
{
    public class MeetingDto
	{
		[JsonPropertyName("id")]
		public long? Id { get; set; }

		[JsonPropertyName("beginning")]
		public DateTime Beginning { get; set; }

		[JsonPropertyName("duration")]
		public int Duration { get; set; }

		[JsonPropertyName("course")]
		public CourseDto Course { get; set; }

		[JsonPropertyName("teacher")]
		public UserDto? Teacher { get; set; }

		[JsonPropertyName("student")]
		public UserDto? Student { get; set; }
	}
}
