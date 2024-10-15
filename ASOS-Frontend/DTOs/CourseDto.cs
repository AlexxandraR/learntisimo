using System.Text.Json.Serialization;
using DTOs.Account;

namespace DTOs
{
    public class CourseDto
	{
		[JsonPropertyName("id")]
		public long? Id { get; set; }

		[JsonPropertyName("name")]
		public string Name { get; set; }

		[JsonPropertyName("price")]
		public double Price { get; set; }

		[JsonPropertyName("room")]
		public string Room { get; set; }

		[JsonPropertyName("teacher")]
		public UserDto? Teacher { get; set; }

		[JsonPropertyName("students")]
		public List<UserDto>? Students { get; set; }
	}
}
