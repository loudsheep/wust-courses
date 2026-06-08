import * as React from "react"
import { cn } from "@/lib/utils"

interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: "default" | "secondary" | "success" | "destructive"
}

function Badge({ className, variant = "default", ...props }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium",
        {
          default: "bg-zinc-800 text-zinc-300",
          secondary: "bg-zinc-700 text-zinc-200",
          success: "bg-emerald-950 text-emerald-400 border border-emerald-900",
          destructive: "bg-red-950 text-red-400 border border-red-900",
        }[variant],
        className
      )}
      {...props}
    />
  )
}

export { Badge }
