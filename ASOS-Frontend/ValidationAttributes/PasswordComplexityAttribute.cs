using System.ComponentModel.DataAnnotations;

namespace ValidationAttributes
{
    public class PasswordComplexityAttribute : ValidationAttribute
    {
        protected override ValidationResult? IsValid(object value, ValidationContext validationContext)
        {
            var password = value as string;

            if (!System.Text.RegularExpressions.Regex.IsMatch(password, @"[0-9]"))
            {
                return new ValidationResult("Heslo musí obsahovať číslicu.");
            }

            if (!System.Text.RegularExpressions.Regex.IsMatch(password, @"[a-z]"))
            {
                return new ValidationResult("Heslo musí obsahovať malé písmeno.");
            }

            if (!System.Text.RegularExpressions.Regex.IsMatch(password, @"[A-Z]"))
            {
                return new ValidationResult("Heslo musí obsahovať veľké písmeno.");
            }

            if (!System.Text.RegularExpressions.Regex.IsMatch(password, @"[!@#$%^&*()_+=]"))
            {
                return new ValidationResult("Heslo musí obsahovať špeciálny znak.");
            }

            if (password.Length < 8)
            {
                return new ValidationResult("Dĺžka hesla musí byť najmenej 8 znakov.");
            }

            return ValidationResult.Success;
        }
    }
}
