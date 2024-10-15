using System.ComponentModel.DataAnnotations;

namespace FormInputModels.ManageAccount
{
    public sealed class ProfileInputModel
    {
        public string? Degree { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné! ")]
        public string Email { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné!")]
        public string FirstName { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné!")]
        public string LastName { get; set; }

        [Required(ErrorMessage = "Toto pole je povinné!")]
        public string PhoneNumber { get; set; }

        public string? Description { get; set; }
    }
}
