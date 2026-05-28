import os
import re
import shutil

base_path = r"e:\sistema veterinario\backend\src\main\java\br\com\clinicavet\clinica_api"

# Target packages
targets = {
    "dto": os.path.join(base_path, "application", "dto"),
    "model": os.path.join(base_path, "domain", "model"),
    "enums": os.path.join(base_path, "domain", "model", "enums"),
    "repository": os.path.join(base_path, "domain", "repository"),
    "jpa": os.path.join(base_path, "infrastructure", "persistence", "jpa"),
    "service": os.path.join(base_path, "application", "service")
}

# Create directories
for name, path in targets.items():
    os.makedirs(path, exist_ok=True)
    print(f"Directory verified/created: {path}")

# Helper functions
def read_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()

def write_file(path, content):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

# 1. Migrate DTOs
old_dto_dir = os.path.join(base_path, "dto")
if os.path.exists(old_dto_dir):
    for filename in os.listdir(old_dto_dir):
        if filename.endswith(".java"):
            old_path = os.path.join(old_dto_dir, filename)
            new_path = os.path.join(targets["dto"], filename)
            content = read_file(old_path)
            content = content.replace("package br.com.clinicavet.clinica_api.dto;", "package br.com.clinicavet.clinica_api.application.dto;")
            write_file(new_path, content)
            os.remove(old_path)
            print(f"Migrated DTO: {filename}")
    try:
        os.rmdir(old_dto_dir)
        print("Removed old DTO directory")
    except Exception as e:
        print(f"Could not remove old DTO directory: {e}")

# 2. Migrate Models & Enums
old_model_dir = os.path.join(base_path, "model")
if os.path.exists(old_model_dir):
    # Process enums first
    old_enums_dir = os.path.join(old_model_dir, "enums")
    if os.path.exists(old_enums_dir):
        for filename in os.listdir(old_enums_dir):
            if filename.endswith(".java"):
                old_path = os.path.join(old_enums_dir, filename)
                new_path = os.path.join(targets["enums"], filename)
                content = read_file(old_path)
                content = content.replace("package br.com.clinicavet.clinica_api.model.enums;", "package br.com.clinicavet.clinica_api.domain.model.enums;")
                write_file(new_path, content)
                os.remove(old_path)
                print(f"Migrated Enum: {filename}")
        try:
            os.rmdir(old_enums_dir)
            print("Removed old enums directory")
        except Exception as e:
            print(f"Could not remove old enums directory: {e}")

    # Process models
    for filename in os.listdir(old_model_dir):
        if filename.endswith(".java"):
            old_path = os.path.join(old_model_dir, filename)
            new_path = os.path.join(targets["model"], filename)
            content = read_file(old_path)
            content = content.replace("package br.com.clinicavet.clinica_api.model;", "package br.com.clinicavet.clinica_api.domain.model;")
            content = content.replace("import br.com.clinicavet.clinica_api.model.enums.", "import br.com.clinicavet.clinica_api.domain.model.enums.")
            write_file(new_path, content)
            os.remove(old_path)
            print(f"Migrated Model: {filename}")
    try:
        os.rmdir(old_model_dir)
        print("Removed old model directory")
    except Exception as e:
        print(f"Could not remove old model directory: {e}")

# 3. Migrate Repositories (Split into Domain and JPA Infrastructure)
old_repo_dir = os.path.join(base_path, "repository")
if os.path.exists(old_repo_dir):
    for filename in os.listdir(old_repo_dir):
        if filename.endswith(".java"):
            old_path = os.path.join(old_repo_dir, filename)
            content = read_file(old_path)
            
            # Find Entity Name and ID Type
            repo_match = re.search(r'public\s+interface\s+([A-Za-z0-9_]+)\s+extends\s+JpaRepository\s*<\s*([A-Za-z0-9_]+)\s*,\s*([A-Za-z0-9_]+)\s*>', content)
            if not repo_match:
                print(f"Could not parse repository pattern in {filename}, copying as-is")
                # Fallback copy
                shutil.copy(old_path, os.path.join(targets["repository"], filename))
                os.remove(old_path)
                continue
            
            repo_name = repo_match.group(1)
            entity_name = repo_match.group(2)
            id_type = repo_match.group(3)
            
            # Parse methods and identify which ones have @Query annotation
            # We want to extract method declarations. A simple way: find all lines and search for @Query.
            # But let's build the Domain repository content and the JPA repository content.
            
            # For Domain Repository:
            # - Package: br.com.clinicavet.clinica_api.domain.repository
            # - Extends: GenericRepository<Entity, ID>
            # - Remove: org.springframework.data.jpa.repository.JpaRepository, @Query, @Param, @Repository annotations, JpaRepository import
            
            domain_content = content
            # Replace package
            domain_content = domain_content.replace("package br.com.clinicavet.clinica_api.repository;", "package br.com.clinicavet.clinica_api.domain.repository;")
            # Add GenericRepository import
            domain_content = re.sub(
                r'(import\s+br\.com\.clinicavet\.clinica_api\.model\..*?;)',
                r'\1\nimport br.com.clinicavet.clinica_api.domain.repository.GenericRepository;',
                domain_content,
                count=1
            )
            # If no model imports, add it after package
            if "import br.com.clinicavet.clinica_api.domain.repository.GenericRepository;" not in domain_content:
                domain_content = domain_content.replace(
                    "package br.com.clinicavet.clinica_api.domain.repository;",
                    "package br.com.clinicavet.clinica_api.domain.repository;\n\nimport br.com.clinicavet.clinica_api.domain.repository.GenericRepository;"
                )
            
            # Remove JpaRepository import
            domain_content = re.sub(r'import\s+org\.springframework\.data\.jpa\.repository\.JpaRepository\s*;', '', domain_content)
            # Remove Repository annotation import
            domain_content = re.sub(r'import\s+org\.springframework\.stereotype\.Repository\s*;', '', domain_content)
            # Remove @Repository annotation
            domain_content = domain_content.replace("@Repository", "")
            # Replace extends JpaRepository with extends GenericRepository
            domain_content = re.sub(
                r'extends\s+JpaRepository\s*<\s*' + re.escape(entity_name) + r'\s*,\s*' + re.escape(id_type) + r'\s*>',
                f"extends GenericRepository<{entity_name}, {id_type}>",
                domain_content
            )
            
            # Remove @Query annotations and @Param annotations inside methods for domain repository
            # Remove @Query(...) - matches multi-line queries as well
            domain_content = re.sub(r'@Query\s*\(\s*".*?"\s*\)', '', domain_content, flags=re.DOTALL)
            domain_content = re.sub(r'@Query\s*\(\s*value\s*=\s*".*?"\s*(,\s*nativeQuery\s*=\s*\w+)?\s*\)', '', domain_content, flags=re.DOTALL)
            domain_content = re.sub(r'@Query\s*\(\s*nativeQuery\s*=\s*\w+\s*,\s*value\s*=\s*".*?"\s*\)', '', domain_content, flags=re.DOTALL)
            domain_content = re.sub(r'@Query\s*\(\s*(""".*?""")\s*\)', '', domain_content, flags=re.DOTALL)
            domain_content = re.sub(r'@Query\s*\(\s*value\s*=\s*(""".*?""")\s*(,\s*nativeQuery\s*=\s*\w+)?\s*\)', '', domain_content, flags=re.DOTALL)
            # Remove @Param("...")
            domain_content = re.sub(r'@Param\s*\(\s*".*?"\s*\)\s*', '', domain_content)
            
            # Save domain repository
            domain_repo_path = os.path.join(targets["repository"], filename)
            write_file(domain_repo_path, domain_content)
            print(f"Created Domain Repository: {filename}")
            
            # For Spring Data JPA Repository:
            # - Package: br.com.clinicavet.clinica_api.infrastructure.persistence.jpa
            # - Class name: SpringDataXRepository
            # - Extends: JpaRepository<Entity, ID>, XRepository
            # - Contains methods with @Query annotations from the original repository
            
            # Let's extract only the methods that have @Query
            # To do this, we can find all methods inside the interface body
            body_match = re.search(r'public\s+interface\s+[A-Za-z0-9_]+\s+extends\s+.*?\{(.*)\}', content, re.DOTALL)
            jpa_methods = []
            if body_match:
                body = body_match.group(1)
                # Split body by semicolon, keeping the block annotations
                # Let's use a simpler heuristic: find @Query and get the corresponding method signature
                # Since we know the methods, let's extract blocks of: (optional annotations) + method signature;
                # Regex to find @Query and the method declaration following it
                query_blocks = re.findall(r'(@Query\s*\(.*?\)\s*[^;]*;)', body, re.DOTALL)
                for qb in query_blocks:
                    jpa_methods.append("    @Override\n    " + qb.strip())
            
            # Let's build SpringDataXRepository
            jpa_repo_name = f"SpringData{repo_name}"
            jpa_content = f"""package br.com.clinicavet.clinica_api.infrastructure.persistence.jpa;

import br.com.clinicavet.clinica_api.domain.model.{entity_name};
import br.com.clinicavet.clinica_api.domain.repository.{repo_name};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
"""
            # Add other imports if they were in the original file
            for line in content.splitlines():
                if line.strip().startswith("import ") and "JpaRepository" not in line and "model." not in line and "repository" not in line and "stereotype.Repository" not in line:
                    jpa_content += line + "\n"
            
            # Ensure Query and Param imports if we have query methods
            if jpa_methods:
                if "org.springframework.data.jpa.repository.Query" not in jpa_content:
                    jpa_content += "import org.springframework.data.jpa.repository.Query;\n"
                if "org.springframework.data.repository.query.Param" not in jpa_content:
                    jpa_content += "import org.springframework.data.repository.query.Param;\n"
            
            jpa_content += f"""
@Repository
public interface {jpa_repo_name} extends JpaRepository<{entity_name}, {id_type}>, {repo_name} {{
"""
            for m in jpa_methods:
                jpa_content += m + "\n\n"
            jpa_content += "}\n"
            
            jpa_repo_path = os.path.join(targets["jpa"], f"{jpa_repo_name}.java")
            write_file(jpa_repo_path, jpa_content)
            print(f"Created SpringData JPA Repository: {jpa_repo_name}.java")
            
            # Remove original repository file
            os.remove(old_path)
            
    try:
        os.rmdir(old_repo_dir)
        print("Removed old repository directory")
    except Exception as e:
        print(f"Could not remove old repository directory: {e}")

# 4. Migrate Services
old_service_dir = os.path.join(base_path, "service")
if os.path.exists(old_service_dir):
    # Process Interface folder first
    old_interface_dir = os.path.join(old_service_dir, "Interface")
    if os.path.exists(old_interface_dir):
        for filename in os.listdir(old_interface_dir):
            if filename.endswith(".java"):
                # If it's BaseService.java, we already created GenericCrudService, so skip and delete
                if filename == "BaseService.java":
                    os.remove(os.path.join(old_interface_dir, filename))
                    continue
                old_path = os.path.join(old_interface_dir, filename)
                new_path = os.path.join(targets["service"], filename)
                content = read_file(old_path)
                content = content.replace("package br.com.clinicavet.clinica_api.service.Interface;", "package br.com.clinicavet.clinica_api.application.service;")
                write_file(new_path, content)
                os.remove(old_path)
                print(f"Migrated Service Interface: {filename}")
        try:
            os.rmdir(old_interface_dir)
            print("Removed old service Interface directory")
        except Exception as e:
            print(f"Could not remove old service Interface directory: {e}")

    # Process service implementations
    for filename in os.listdir(old_service_dir):
        if filename.endswith(".java"):
            # Skip BaseServiceImplement.java since we replaced it with GenericCrudServiceImplement.java
            if filename == "BaseServiceImplement.java":
                os.remove(os.path.join(old_service_dir, filename))
                continue
            old_path = os.path.join(old_service_dir, filename)
            new_path = os.path.join(targets["service"], filename)
            content = read_file(old_path)
            content = content.replace("package br.com.clinicavet.clinica_api.service;", "package br.com.clinicavet.clinica_api.application.service;")
            write_file(new_path, content)
            os.remove(old_path)
            print(f"Migrated Service Implement: {filename}")
    try:
        os.rmdir(old_service_dir)
        print("Removed old service directory")
    except Exception as e:
        print(f"Could not remove old service directory: {e}")

# 5. Recursively update all references/imports in all files in clinicavet
def update_imports_in_dir(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                path = os.path.join(root, file)
                content = read_file(path)
                original = content
                
                # Perform package/import replacements
                # Replace Execeptions/exceptions
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.Execeptions\b', 'br.com.clinicavet.clinica_api.api.exception', content)
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.exceptions\b', 'br.com.clinicavet.clinica_api.api.exception', content)
                
                # Replace dto
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.dto\b', 'br.com.clinicavet.clinica_api.application.dto', content)
                
                # Replace model
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.model\b', 'br.com.clinicavet.clinica_api.domain.model', content)
                
                # Replace repository
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.repository\b', 'br.com.clinicavet.clinica_api.domain.repository', content)
                
                # Replace service (both Interface and main package)
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.service\.Interface\b', 'br.com.clinicavet.clinica_api.application.service', content)
                content = re.sub(r'\bbr\.com\.clinicavet\.clinica_api\.service\b', 'br.com.clinicavet.clinica_api.application.service', content)
                
                # Replace BaseService -> GenericCrudService
                content = content.replace("extends BaseService<", "extends GenericCrudService<")
                content = content.replace("implements BaseService<", "implements GenericCrudService<")
                content = content.replace("extends BaseServiceImplement<", "extends GenericCrudServiceImplement<")
                content = content.replace("import br.com.clinicavet.clinica_api.application.service.BaseService;", "import br.com.clinicavet.clinica_api.application.service.GenericCrudService;")
                
                # Fix self-imports (e.g. if application/service/SomeService imports br.com.clinicavet.clinica_api.application.service.SomeService, remove it)
                package_match = re.search(r'package\s+([a-zA-Z0-9_\.]+);', content)
                if package_match:
                    pkg = package_match.group(1)
                    class_name = file.replace(".java", "")
                    self_import = f"import {pkg}.{class_name};"
                    content = content.replace(self_import, "")
                    # Also replace other self imports in same package
                    self_import_wildcard = f"import {pkg}.*;"
                    content = content.replace(self_import_wildcard, "")
                
                if content != original:
                    write_file(path, content)
                    print(f"Updated imports in {file}")

update_imports_in_dir(base_path)
test_path = r"e:\sistema veterinario\backend\src\test\java"
if os.path.exists(test_path):
    update_imports_in_dir(test_path)

print("Migration completed successfully!")
