# Tomorrow TODO (Resume Checklist)

**Date**: March 24, 2026  
**Primary Goal**: Continue Phase 3 decomposition safely with compile-verified increments.

## Start-of-Session Checks

1. Verify git state and isolate intended files only.
2. Re-open `PROGRAM_STATUS.md` and confirm priorities.
3. Re-scan for remaining broad coupling points in repository/service layers.

## Priority Work Items

1. Continue `MenuRepositoryCustomImpl` decomposition.
2. Reduce remaining high-coupling methods behind focused adapter calls.
3. Prune interface usage where now redundant, without breaking callers.
4. Run compile checks after each small slice.
5. Update reports only after code status is validated.

## Suggested Command Sequence

```powershell
Get-Location
Get-ChildItem -Name *.md | Sort-Object
git status --short
mvn -DskipTests compile
```

## Validation Checklist

- [ ] Build compiles successfully after each change set.
- [ ] No unrelated files included in staged changes.
- [ ] Updated status reflected in `PROGRAM_STATUS.md`.
- [ ] Any phase report touched includes the status sync pointer.

## End-of-Session Deliverables

1. Short change summary of code files modified.
2. Compile result statement (success/failure + key error summary if failed).
3. Updated remaining-work notes per phase.
